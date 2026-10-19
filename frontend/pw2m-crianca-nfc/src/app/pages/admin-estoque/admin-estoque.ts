import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { DevicesService } from '../../core/services/devices.service';
import { ManufacturedDevice, NfcDeviceCategory } from '../../core/models/nfc-device.models';

@Component({
  selector: 'app-admin-estoque',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-estoque.html',
  styleUrl: '../nfc-devices/nfc-devices.scss'
})
export class AdminEstoque {

  private readonly fb = inject(FormBuilder);
  private readonly devices = inject(DevicesService);

  resultado = signal<ManufacturedDevice[]>([]);
  salvando = signal(false);
  erro = signal('');

  categorias: NfcDeviceCategory[] = [
    'CHAVEIRO', 'PULSEIRA', 'MOCHILA', 'CARTAO', 'ETIQUETA', 'OUTRO'
  ];

  formulario = this.fb.nonNullable.group({
    quantity: [1, [Validators.required, Validators.min(1), Validators.max(500)]],
    category: this.fb.nonNullable.control<NfcDeviceCategory>('CHAVEIRO', Validators.required)
  });

  async fabricar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.erro.set('');
    this.salvando.set(true);
    try {
      const dados = this.formulario.getRawValue();
      const lote = await firstValueFrom(
        this.devices.manufacture(dados.quantity, dados.category)
      );
      this.resultado.set(lote);
    } catch (erro: unknown) {
      const message = (erro as { error?: { message?: string } })?.error?.message;
      this.erro.set(message ?? 'Não foi possível fabricar o lote.');
    } finally {
      this.salvando.set(false);
    }
  }
}
