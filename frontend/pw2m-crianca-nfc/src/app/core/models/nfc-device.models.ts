export type NfcDeviceCategory =
  | 'MOCHILA'
  | 'PULSEIRA'
  | 'CHAVEIRO'
  | 'CARTAO'
  | 'ETIQUETA'
  | 'OUTRO';

export type NfcDeviceStatus =
  | 'AVAILABLE'
  | 'CLAIMED'
  | 'ACTIVE'
  | 'SUSPENDED'
  | 'LOST'
  | 'BLOCKED';

export interface NfcDevice {
  id: number;
  deviceName: string;
  category: NfcDeviceCategory;
  deviceToken: string;
  active: boolean;
  createdAt: string;
}

export interface OwnedDevice {
  id: number;
  deviceCode: string;
  deviceName: string;
  category: NfcDeviceCategory;
  status: NfcDeviceStatus;
  personId: number | null;
  personName: string | null;
  claimedAt: string | null;
  manufacturedAt: string;
}

export interface ClaimDeviceRequest {
  deviceCode: string;
  activationCode: string;
}

export interface AssignDeviceRequest {
  personId: number;
  deviceName: string;
}

export interface ManufacturedDevice {
  id: number;
  deviceCode: string;
  activationCode: string;
  category: NfcDeviceCategory;
  status: NfcDeviceStatus;
  manufacturedAt: string;
}
