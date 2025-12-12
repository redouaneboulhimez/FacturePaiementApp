import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <div class="header">
      <h1>💰 Application de Gestion de Factures et Paiements</h1>
    </div>
    <nav class="nav">
      <ul>
        <li><a routerLink="/dashboard" routerLinkActive="active">Dashboard</a></li>
        <li><a routerLink="/clients" routerLinkActive="active">Clients</a></li>
        <li><a routerLink="/factures" routerLinkActive="active">Factures</a></li>
        <li><a routerLink="/paiements" routerLinkActive="active">Paiements</a></li>
        <li><a routerLink="/notifications" routerLinkActive="active">Notifications</a></li>
      </ul>
    </nav>
    <main class="container">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: []
})
export class AppComponent {
  title = 'Facture Paiement App';
}

