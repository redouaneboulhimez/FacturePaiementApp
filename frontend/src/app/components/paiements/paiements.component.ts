import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Paiement, Facture } from '../../services/api.service';

@Component({
  selector: 'app-paiements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>💳 Gestion des Paiements</h2>
      
      <button class="btn btn-primary" (click)="showForm = !showForm">
        {{ showForm ? 'Annuler' : 'Nouveau Paiement' }}
      </button>

      <div *ngIf="showForm" class="card" style="margin-top: 1rem;">
        <h3>Effectuer un Paiement</h3>
        <form (ngSubmit)="effectuerPaiement()">
          <div class="form-group">
            <label>Facture *</label>
            <select [(ngModel)]="selectedFactureId" name="factureId" (change)="onFactureChange()" required>
              <option value="">Sélectionner une facture</option>
              <option *ngFor="let facture of facturesEnAttente" [value]="facture.id">
                Facture #{{ facture.id }} - {{ facture.montant | number:'1.2-2' }} € - Client: {{ facture.clientId }}
              </option>
            </select>
          </div>
          <div *ngIf="selectedFacture" class="form-group">
            <label>Montant à payer</label>
            <input type="text" [value]="selectedFacture.montant | number:'1.2-2'" readonly class="readonly-input">
            <small>Montant de la facture: {{ selectedFacture.montant | number:'1.2-2' }} €</small>
          </div>
          <div class="form-group">
            <label>Méthode de paiement *</label>
            <select [(ngModel)]="paiement.methodePaiement" name="methodePaiement" required>
              <option value="VIREMENT">Virement</option>
              <option value="CARTE">Carte bancaire</option>
              <option value="CHEQUE">Chèque</option>
              <option value="ESPECES">Espèces</option>
            </select>
          </div>
          <button type="submit" class="btn btn-success" [disabled]="!selectedFacture">Payer</button>
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
            <th>Facture ID</th>
            <th>Montant</th>
            <th>Date Paiement</th>
            <th>Méthode</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let paiement of paiements">
            <td>{{ paiement.id }}</td>
            <td>{{ paiement.factureId }}</td>
            <td>{{ paiement.montant | number:'1.2-2' }} €</td>
            <td>{{ paiement.datePaiement | date:'short' }}</td>
            <td>{{ paiement.methodePaiement || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .readonly-input {
      background-color: #f5f5f5;
      cursor: not-allowed;
      border: 1px solid #ddd;
      padding: 0.5rem;
      border-radius: 4px;
    }
  `]
})
export class PaiementsComponent implements OnInit {
  paiements: Paiement[] = [];
  facturesEnAttente: Facture[] = [];
  selectedFactureId: number | null = null;
  selectedFacture: Facture | null = null;
  paiement: Paiement = {
    factureId: 0,
    montant: 0,
    methodePaiement: 'VIREMENT'
  };
  showForm = false;
  loading = true;
  message = '';
  messageType: 'success' | 'error' = 'success';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadPaiements();
    this.loadFacturesEnAttente();
  }

  loadPaiements() {
    this.loading = true;
    this.apiService.getPaiements().subscribe({
      next: (data) => {
        this.paiements = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur:', err);
        this.showMessage('Erreur lors du chargement des paiements', 'error');
        this.loading = false;
      }
    });
  }

  loadFacturesEnAttente() {
    this.apiService.getFacturesByStatut('EN_ATTENTE').subscribe({
      next: (data) => {
        this.facturesEnAttente = data;
      },
      error: (err) => console.error('Erreur chargement factures:', err)
    });
  }

  onFactureChange() {
    if (this.selectedFactureId) {
      this.apiService.getFacture(this.selectedFactureId).subscribe({
        next: (facture) => {
          this.selectedFacture = facture;
          // Le montant sera utilisé directement depuis selectedFacture lors de l'envoi
        },
        error: (err) => console.error('Erreur:', err)
      });
    }
  }

  effectuerPaiement() {
    if (!this.selectedFacture) return;
    
    // S'assurer que le montant est bien un nombre (conversion explicite pour éviter les problèmes de précision)
    const montant = Number(this.selectedFacture.montant);
    
    // Log pour déboguer
    console.log('Paiement - Facture ID:', this.selectedFacture.id);
    console.log('Paiement - Montant envoyé:', montant, 'Type:', typeof montant);
    console.log('Paiement - Montant facture original:', this.selectedFacture.montant);
    
    // Utiliser directement le montant de la facture sélectionnée pour éviter les problèmes de précision
    this.apiService.effectuerPaiement({
      factureId: this.selectedFacture.id!,
      montant: montant,
      methodePaiement: this.paiement.methodePaiement || 'VIREMENT'
    }).subscribe({
      next: () => {
        this.showMessage('Paiement effectué avec succès !', 'success');
        this.resetForm();
        this.loadPaiements();
        this.loadFacturesEnAttente();
      },
      error: (err) => {
        console.error('Erreur paiement complète:', err);
        let errorMsg = 'Erreur lors du paiement';
        
        if (err.status === 0) {
          errorMsg = 'Impossible de se connecter au serveur. Vérifiez que tous les services sont démarrés.';
        } else if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.error?.message) {
          errorMsg = err.error.message;
        } else if (err.message) {
          errorMsg = err.message;
        }
        
        this.showMessage(errorMsg, 'error');
      }
    });
  }

  resetForm() {
    this.paiement = {
      factureId: 0,
      montant: 0,
      methodePaiement: 'VIREMENT'
    };
    this.selectedFactureId = null;
    this.selectedFacture = null;
    this.showForm = false;
  }

  showMessage(msg: string, type: 'success' | 'error') {
    this.message = msg;
    this.messageType = type;
    setTimeout(() => this.message = '', 5000);
  }
}

