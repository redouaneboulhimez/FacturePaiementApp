import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Facture, Client } from '../../services/api.service';

@Component({
  selector: 'app-factures',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>📄 Gestion des Factures</h2>
      
      <button class="btn btn-primary" (click)="showForm = !showForm">
        {{ showForm ? 'Annuler' : 'Nouvelle Facture' }}
      </button>

      <div *ngIf="showForm" class="card" style="margin-top: 1rem;">
        <h3>{{ editingFacture ? 'Modifier' : 'Créer' }} une Facture</h3>
        <form (ngSubmit)="saveFacture()">
          <div class="form-group">
            <label>Client *</label>
            <select [(ngModel)]="facture.clientId" name="clientId" required>
              <option value="">Sélectionner un client</option>
              <option *ngFor="let client of clients" [value]="client.id">{{ client.nom }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Montant *</label>
            <input type="number" step="0.01" [(ngModel)]="facture.montant" name="montant" required>
          </div>
          <div class="form-group">
            <label>Date d'émission *</label>
            <input type="date" [(ngModel)]="facture.dateEmission" name="dateEmission" required>
          </div>
          <div class="form-group">
            <label>Date d'échéance *</label>
            <input type="date" [(ngModel)]="facture.dateEcheance" name="dateEcheance" required>
          </div>
          <div class="form-group">
            <label>Description</label>
            <input type="text" [(ngModel)]="facture.description" name="description">
          </div>
          <button type="submit" class="btn btn-success">{{ editingFacture ? 'Modifier' : 'Créer' }}</button>
        </form>
      </div>

      <div *ngIf="message" [class]="'alert ' + (messageType === 'success' ? 'alert-success' : 'alert-error')">
        {{ message }}
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>

      <table *ngIf="!loading" class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Client ID</th>
            <th>Montant</th>
            <th>Date Émission</th>
            <th>Date Échéance</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let facture of factures">
            <td>{{ facture.id }}</td>
            <td>{{ facture.clientId }}</td>
            <td>{{ facture.montant | number:'1.2-2' }} €</td>
            <td>{{ facture.dateEmission }}</td>
            <td>{{ facture.dateEcheance }}</td>
            <td>
              <span [class]="'badge badge-' + getStatutClass(facture.statut)">
                {{ facture.statut }}
              </span>
            </td>
            <td>
              <button class="btn btn-primary" (click)="viewFacture(facture)">Voir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: []
})
export class FacturesComponent implements OnInit {
  factures: Facture[] = [];
  clients: Client[] = [];
  facture: Facture = {
    clientId: 0,
    montant: 0,
    dateEmission: '',
    dateEcheance: '',
    statut: 'EN_ATTENTE',
    description: ''
  };
  showForm = false;
  editingFacture: Facture | null = null;
  loading = true;
  message = '';
  messageType: 'success' | 'error' = 'success';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadFactures();
    this.loadClients();
  }

  loadFactures() {
    this.loading = true;
    this.apiService.getFactures().subscribe({
      next: (data) => {
        this.factures = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement factures:', err);
        let errorMsg = 'Erreur lors du chargement des factures';
        if (err.status === 0) {
          errorMsg = 'Impossible de se connecter au serveur. Vérifiez que l\'API Gateway est démarré.';
        } else if (err.error?.error) {
          errorMsg = err.error.error;
        }
        this.showMessage(errorMsg, 'error');
        this.loading = false;
      }
    });
  }

  loadClients() {
    this.apiService.getClients().subscribe({
      next: (data) => {
        this.clients = data;
      },
      error: (err) => console.error('Erreur chargement clients:', err)
    });
  }

  saveFacture() {
    this.facture.statut = 'EN_ATTENTE';
    this.apiService.createFacture(this.facture).subscribe({
      next: () => {
        this.showMessage('Facture créée avec succès', 'success');
        this.resetForm();
        this.loadFactures();
      },
      error: (err) => {
        console.error('Erreur création facture:', err);
        const errorMsg = err.error?.message || err.error?.error || err.message || 'Erreur lors de la création';
        this.showMessage(errorMsg, 'error');
      }
    });
  }

  viewFacture(facture: Facture) {
    alert(`Facture #${facture.id}\nClient: ${facture.clientId}\nMontant: ${facture.montant}€\nStatut: ${facture.statut}`);
  }

  getStatutClass(statut: string): string {
    switch(statut) {
      case 'PAYEE': return 'success';
      case 'EN_RETARD': return 'danger';
      default: return 'warning';
    }
  }

  resetForm() {
    this.facture = {
      clientId: 0,
      montant: 0,
      dateEmission: '',
      dateEcheance: '',
      statut: 'EN_ATTENTE',
      description: ''
    };
    this.showForm = false;
  }

  showMessage(msg: string, type: 'success' | 'error') {
    this.message = msg;
    this.messageType = type;
    setTimeout(() => this.message = '', 3000);
  }
}

