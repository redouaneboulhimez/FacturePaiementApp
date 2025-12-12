import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, Notification } from '../../services/api.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>🔔 Notifications</h2>

      <div *ngIf="loading" class="loading">Chargement...</div>

      <table *ngIf="!loading" class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Type</th>
            <th>Facture ID</th>
            <th>Email Client</th>
            <th>Montant</th>
            <th>Date Envoi</th>
            <th>Statut</th>
            <th>Message</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let notification of notifications">
            <td>{{ notification.id }}</td>
            <td>
              <span [class]="'badge badge-' + (notification.type === 'PAIEMENT' ? 'success' : 'warning')">
                {{ notification.type }}
              </span>
            </td>
            <td>{{ notification.factureId }}</td>
            <td>{{ notification.clientEmail }}</td>
            <td>{{ notification.montant | number:'1.2-2' }} €</td>
            <td>{{ notification.dateEnvoi | date:'short' }}</td>
            <td>
              <span [class]="'badge badge-' + (notification.statut === 'ENVOYE' ? 'success' : 'danger')">
                {{ notification.statut }}
              </span>
            </td>
            <td>{{ notification.message || '-' }}</td>
          </tr>
        </tbody>
      </table>

      <div *ngIf="!loading && notifications.length === 0" class="alert alert-success">
        Aucune notification pour le moment.
      </div>
    </div>
  `,
  styles: []
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.loading = true;
    this.apiService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur:', err);
        this.loading = false;
      }
    });
  }
}

