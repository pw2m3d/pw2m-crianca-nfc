import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from '../../../environments/environment';

import {
  ChildPayload,
  ChildResponse,
  ChildSummary,
  PublicChild
} from '../models/child.models';

@Injectable({
  providedIn: 'root'
})
export class ChildrenService {

  constructor(
    private http: HttpClient
  ) {}

  list() {
    return this.http.get<ChildSummary[]>(
      `${environment.apiUrl}/children`
    );
  }

  get(id: number) {
    return this.http.get<ChildResponse>(
      `${environment.apiUrl}/children/${id}`
    );
  }

  create(payload: ChildPayload) {
    return this.http.post<ChildResponse>(
      `${environment.apiUrl}/children`,
      payload
    );
  }

  update(
    id: number,
    payload: ChildPayload
  ) {
    return this.http.put<ChildResponse>(
      `${environment.apiUrl}/children/${id}`,
      payload
    );
  }

  delete(id: number) {
    return this.http.delete<void>(
      `${environment.apiUrl}/children/${id}`
    );
  }

  getPublic(publicToken: string) {
    return this.http.get<PublicChild>(
      `${environment.apiUrl}/public/children/${publicToken}`
    );
  }

  getPublicByDevice(deviceToken: string) {
    return this.http.get<PublicChild>(
      `${environment.apiUrl}/public/devices/${deviceToken}`
    );
  }
}