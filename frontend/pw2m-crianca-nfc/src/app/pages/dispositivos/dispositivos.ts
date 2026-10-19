import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { DevicesService } from '../../core/services/devices.service';
import { OwnedDevice } from '../../core/models/nfc-device.models';

@Component({
  selector: 'app-dispositivos',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './dispositivos.html',
  styleUrl: '../nfc-devices/nfc-devices.scss'
})
export class Dispositivos implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly devices = inject(DevicesService);

  lista = signal<OwnedDevice[]>([]);
  carregando = signal(true);
  salvando = signal(false);
  erro = signal('');
  ok = signal('');

  formulario = this.fb.nonNullable.group({
    deviceCode: ['', Validators.required],
    activationCode: ['', [Validators.required, Validators.minLength(6)]]
  });

  ngOnInit(): void {
    void this.carregar();
  }

  async carregar(): Promise<void> {
    this.carregando.set(true);
    try {
      const dados = await firstValueFrom(this.devices.listMine());
      this.lista.set(dados);
    } catch {
      this.erro.set('Não foi possível listar os chaveiros.');
    } finally {
      this.carregando.set(false);
    }
  }

  async ativar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.erro.set('');
    this.ok.set('');
    this.salvando.set(true);
    try {
      const dados = this.formulario.getRawValue();
      await firstValueFrom(
        this.devices.claim({
          deviceCode: dados.deviceCode.trim(),
          activationCode: dados.activationCode.trim()
        })
      );
      this.formulario.reset({ deviceCode: '', activationCode: '' });
      this.ok.set('Chaveiro ativado. Vincule-o a uma pessoa na ficha.');
      await this.carregar();
    } catch (erro: unknown) {
      const message = (erro as { error?: { message?: string } })?.error?.message;
      this.erro.set(message ?? 'Não foi possível ativar o chaveiro.');
    } finally {
      this.salvando.set(false);
    }
  }

  linkNfc(item: OwnedDevice): string {
    return `${window.location.origin}/d/${item.deviceCode}`;
  }
}
