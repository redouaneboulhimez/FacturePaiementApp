import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>📊 Dashboard</h2>
      
      <div class="stats">
        <div class="stat-card">
          <h3>Total Clients</h3>
          <div class="value">{{ stats.totalClients }}</div>
        </div>
        <div class="stat-card">
          <h3>Factures Payées</h3>
          <div class="value">{{ stats.facturesPayees }}</div>
        </div>
        <div class="stat-card">
          <h3>Factures Impayées</h3>
          <div class="value">{{ stats.facturesImpayees }}</div>
        </div>
        <div class="stat-card">
          <h3>Factures en Retard</h3>
          <div class="value">{{ stats.facturesEnRetard }}</div>
        </div>
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>
    </div>
  `,
  styles: []
})
export class DashboardComponent implements OnInit {
  stats = {
    totalClients: 0,
    facturesPayees: 0,
    facturesImpayees: 0,
    facturesEnRetard: 0
  };
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.loading = true;
    let clientsLoaded = false;
    let facturesLoaded = false;
    
    const checkComplete = () => {
      if (clientsLoaded && facturesLoaded) {
        this.loading = false;
      }
    };
    
    this.apiService.getClients().subscribe({
      next: (clients) => {
        this.stats.totalClients = clients.length;
        clientsLoaded = true;
        checkComplete();
      },
      error: (err) => {
        console.error('Erreur chargement clients:', err);
        if (err.status === 0) {
          console.error('Impossible de se connecter au serveur. Vérifiez que l\'API Gateway est démarré.');
        }
        clientsLoaded = true;
        checkComplete();
      }
    });

    this.apiService.getFactures().subscribe({
      next: (factures) => {
        this.stats.facturesPayees = factures.filter(f => f.statut === 'PAYEE').length;
        this.stats.facturesImpayees = factures.filter(f => f.statut === 'EN_ATTENTE').length;
        this.stats.facturesEnRetard = factures.filter(f => f.statut === 'EN_RETARD').length;
        facturesLoaded = true;
        checkComplete();
      },
      error: (err) => {
        console.error('Erreur chargement factures:', err);
        if (err.status === 0) {
          console.error('Impossible de se connecter au serveur. Vérifiez que l\'API Gateway est démarré.');
        }
        facturesLoaded = true;
        checkComplete();
      }
    });
  }
}

