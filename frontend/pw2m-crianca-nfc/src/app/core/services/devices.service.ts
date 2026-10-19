import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  AssignDeviceRequest,
  ClaimDeviceRequest,
  ManufacturedDevice,
  NfcDeviceCategory,
  OwnedDevice
} from '../models/nfc-device.models';

@Injectable({
  providedIn: 'root'
})
export class DevicesService {

  constructor(private http: HttpClient) {}

  claim(payload: ClaimDeviceRequest) {
    return this.http.post<OwnedDevice>(
      `${environment.apiUrl}/devices/claim`,
      payload
    );
  }

  listMine() {
    return this.http.get<OwnedDevice[]>(
      `${environment.apiUrl}/devices`
    );
  }

  assign(deviceId: number, payload: AssignDeviceRequest) {
    return this.http.put<OwnedDevice>(
      `${environment.apiUrl}/devices/${deviceId}/assign`,
      payload
    );
  }

  unassign(deviceId: number) {
    return this.http.put<OwnedDevice>(
      `${environment.apiUrl}/devices/${deviceId}/unassign`,
      {}
    );
  }

  manufacture(quantity: number, category: NfcDeviceCategory) {
    return this.http.post<ManufacturedDevice[]>(
      `${environment.apiUrl}/admin/devices/manufacture`,
      { quantity, category }
    );
  }
}
