import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Client } from '../../services/api.service';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>👥 Gestion des Clients</h2>
      
      <button class="btn btn-primary" (click)="showForm = !showForm">
        {{ showForm ? 'Annuler' : 'Nouveau Client' }}
      </button>

      <div *ngIf="showForm" class="card" style="margin-top: 1rem;">
        <h3>{{ editingClient ? 'Modifier' : 'Créer' }} un Client</h3>
        <form (ngSubmit)="saveClient()">
          <div class="form-group">
            <label>Nom *</label>
            <input type="text" [(ngModel)]="client.nom" name="nom" required>
          </div>
          <div class="form-group">
            <label>Email *</label>
            <input type="email" [(ngModel)]="client.email" name="email" required>
          </div>
          <div class="form-group">
            <label>Téléphone</label>
            <input type="text" [(ngModel)]="client.telephone" name="telephone">
          </div>
          <div class="form-group">
            <label>Adresse</label>
            <input type="text" [(ngModel)]="client.adresse" name="adresse">
          </div>
          <button type="submit" class="btn btn-success">{{ editingClient ? 'Modifier' : 'Créer' }}</button>
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
            <th>Nom</th>
            <th>Email</th>
            <th>Téléphone</th>
            <th>Adresse</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let client of clients">
            <td>{{ client.id }}</td>
            <td>{{ client.nom }}</td>
            <td>{{ client.email }}</td>
            <td>{{ client.telephone || '-' }}</td>
            <td>{{ client.adresse || '-' }}</td>
            <td>
              <button class="btn btn-primary" (click)="editClient(client)">Modifier</button>
              <button class="btn btn-danger" (click)="deleteClient(client.id!)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: []
})
export class ClientsComponent implements OnInit {
  clients: Client[] = [];
  client: Client = { nom: '', email: '', telephone: '', adresse: '' };
  showForm = false;
  editingClient: Client | null = null;
  loading = true;
  message = '';
  messageType: 'success' | 'error' = 'success';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadClients();
  }

  loadClients() {
    this.loading = true;
    this.apiService.getClients().subscribe({
      next: (data) => {
        this.clients = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement clients:', err);
        let errorMsg = 'Erreur lors du chargement des clients';
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

  saveClient() {
    if (this.editingClient) {
      this.apiService.updateClient(this.editingClient.id!, this.client).subscribe({
        next: () => {
          this.showMessage('Client modifié avec succès', 'success');
          this.resetForm();
          this.loadClients();
        },
        error: (err) => this.showMessage('Erreur lors de la modification', 'error')
      });
    } else {
      this.apiService.createClient(this.client).subscribe({
        next: () => {
          this.showMessage('Client créé avec succès', 'success');
          this.resetForm();
          this.loadClients();
        },
        error: (err) => this.showMessage('Erreur lors de la création', 'error')
      });
    }
  }

  editClient(client: Client) {
    this.editingClient = client;
    this.client = { ...client };
    this.showForm = true;
  }

  deleteClient(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.apiService.deleteClient(id).subscribe({
        next: () => {
          this.showMessage('Client supprimé avec succès', 'success');
          this.loadClients();
        },
        error: (err) => this.showMessage('Erreur lors de la suppression', 'error')
      });
    }
  }

  resetForm() {
    this.client = { nom: '', email: '', telephone: '', adresse: '' };
    this.editingClient = null;
    this.showForm = false;
  }

  showMessage(msg: string, type: 'success' | 'error') {
    this.message = msg;
    this.messageType = type;
    setTimeout(() => this.message = '', 3000);
  }
}

