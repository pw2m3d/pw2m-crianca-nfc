import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { NfcDevice } from '../models/nfc-device.models';

@Injectable({
  providedIn: 'root'
})
export class NfcDeviceService {

  constructor(private http: HttpClient) {}

  list(childId: number) {
    return this.http.get<NfcDevice[]>(
      `${environment.apiUrl}/children/${childId}/devices`
    );
  }

  toggle(childId: number, deviceId: number) {
    return this.http.patch<NfcDevice>(
      `${environment.apiUrl}/children/${childId}/devices/${deviceId}/toggle`,
      {}
    );
  }
}
