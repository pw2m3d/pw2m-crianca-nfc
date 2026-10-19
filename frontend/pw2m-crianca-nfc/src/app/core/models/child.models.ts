export interface EmergencyContact {
  id?: number;
  name: string;
  relation: string;
  phone: string;
  whatsapp?: string | null;
  priority?: number;
}

export interface MedicalInfo {
  allergies?: string | null;
  conditions?: string | null;
  medications?: string | null;
  healthPlan?: string | null;
  healthPlanNumber?: string | null;
  emergencyNotes?: string | null;
}

export type PersonType = 'CHILD' | 'ELDERLY' | 'ADULT' | 'OTHER';

export interface ChildPayload {
  fullName: string;
  personType?: PersonType;
  birthDate?: string | null;
  bloodType?: string | null;
  photoUrl?: string | null;

  // Privados: ficam armazenados, mas não aparecem na ficha pública
  addressLine?: string | null;
  city?: string | null;
  state?: string | null;

  publicEnabled?: boolean;

  medical?: MedicalInfo | null;
  contacts?: EmergencyContact[];
}

export interface ChildSummary {
  id: number;
  publicToken: string;
  fullName: string;
  personType?: PersonType;
  ageYears?: number | null;
  bloodType?: string | null;
  publicEnabled: boolean;
}

export interface ChildResponse {
  id: number;
  publicToken: string;
  fullName: string;
  personType?: PersonType;
  birthDate?: string | null;
  ageYears?: number | null;
  bloodType?: string | null;
  photoUrl?: string | null;
  addressLine?: string | null;
  city?: string | null;
  state?: string | null;
  publicEnabled: boolean;
  medical?: MedicalInfo | null;
  contacts: EmergencyContact[];
}

export interface PublicChild {
  name: string;
  ageYears?: number | null;
  bloodType?: string | null;
  photoUrl?: string | null;
  medical?: MedicalInfo | null;
  contacts: EmergencyContact[];
}