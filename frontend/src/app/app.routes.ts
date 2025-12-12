import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ClientsComponent } from './components/clients/clients.component';
import { FacturesComponent } from './components/factures/factures.component';
import { PaiementsComponent } from './components/paiements/paiements.component';
import { NotificationsComponent } from './components/notifications/notifications.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'clients', component: ClientsComponent },
  { path: 'factures', component: FacturesComponent },
  { path: 'paiements', component: PaiementsComponent },
  { path: 'notifications', component: NotificationsComponent }
];

