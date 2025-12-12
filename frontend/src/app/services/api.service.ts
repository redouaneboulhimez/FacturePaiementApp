import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

export interface Client {
  id?: number;
  nom: string;
  email: string;
  telephone?: string;
  adresse?: string;
}

export interface Facture {
  id?: number;
  clientId: number;
  montant: number;
  dateEmission: string;
  dateEcheance: string;
  statut: 'EN_ATTENTE' | 'PAYEE' | 'EN_RETARD';
  description?: string;
}

export interface Paiement {
  id?: number;
  factureId: number;
  montant: number;
  datePaiement?: string;
  methodePaiement?: string;
}

export interface Notification {
  id?: number;
  paiementId?: number;
  factureId: number;
  clientEmail: string;
  montant: number;
  type: 'PAIEMENT' | 'RELANCE';
  dateEnvoi?: string;
  statut: string;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  // Clients
  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${API_URL}/clients`);
  }

  getClient(id: number): Observable<Client> {
    return this.http.get<Client>(`${API_URL}/clients/${id}`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(`${API_URL}/clients`, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${API_URL}/clients/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/clients/${id}`);
  }

  searchClients(nom?: string, email?: string): Observable<Client[]> {
    const params: any = {};
    if (nom) params.nom = nom;
    if (email) params.email = email;
    return this.http.get<Client[]>(`${API_URL}/clients/search`, { params });
  }

  // Factures
  getFactures(): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${API_URL}/factures`);
  }

  getFacture(id: number): Observable<Facture> {
    return this.http.get<Facture>(`${API_URL}/factures/${id}`);
  }

  getFacturesByClient(clientId: number): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${API_URL}/factures/client/${clientId}`);
  }

  createFacture(facture: Facture): Observable<Facture> {
    return this.http.post<Facture>(`${API_URL}/factures`, facture);
  }

  updateFactureStatus(id: number, statut: string): Observable<Facture> {
    return this.http.put<Facture>(`${API_URL}/factures/${id}/status?statut=${statut}`, {});
  }

  getFacturesByStatut(statut: string): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${API_URL}/factures/statut/${statut}`);
  }

  // Paiements
  getPaiements(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${API_URL}/paiements`);
  }

  getPaiement(id: number): Observable<Paiement> {
    return this.http.get<Paiement>(`${API_URL}/paiements/${id}`);
  }

  getPaiementsByFacture(factureId: number): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${API_URL}/paiements/facture/${factureId}`);
  }

  effectuerPaiement(paiement: { factureId: number; montant: number; methodePaiement: string }): Observable<Paiement> {
    return this.http.post<Paiement>(`${API_URL}/paiements`, paiement);
  }

  // Notifications
  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${API_URL}/notifications`);
  }

  getNotificationsByClient(email: string): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${API_URL}/notifications/client/${email}`);
  }
}

