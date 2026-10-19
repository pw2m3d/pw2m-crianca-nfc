import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ChildrenService } from '../../core/services/children.service';
import { NfcDeviceService } from '../../core/services/nfc-device.service';
import { DevicesService } from '../../core/services/devices.service';
import { ChildResponse } from '../../core/models/child.models';
import { NfcDevice, NfcDeviceCategory, OwnedDevice } from '../../core/models/nfc-device.models';

@Component({
  selector: 'app-nfc-devices',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nfc-devices.html',
  styleUrl: './nfc-devices.scss'
})
export class NfcDevices implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly childrenService = inject(ChildrenService);
  private readonly nfcService = inject(NfcDeviceService);
  private readonly devicesService = inject(DevicesService);

  childId = 0;
  crianca = signal<ChildResponse | null>(null);
  dispositivos = signal<NfcDevice[]>([]);
  disponiveis = signal<OwnedDevice[]>([]);
  carregando = signal(true);
  salvando = signal(false);
  erro = signal('');

  categorias: Array<{ value: NfcDeviceCategory; label: string }> = [
    { value: 'MOCHILA', label: 'Mochila' },
    { value: 'PULSEIRA', label: 'Pulseira' },
    { value: 'CHAVEIRO', label: 'Chaveiro' },
    { value: 'CARTAO', label: 'Cartão' },
    { value: 'ETIQUETA', label: 'Etiqueta' },
    { value: 'OUTRO', label: 'Outro' }
  ];

  formulario = this.fb.nonNullable.group({
    deviceId: ['', Validators.required],
    deviceName: ['', [Validators.required, Validators.minLength(3)]]
  });

  async ngOnInit(): Promise<void> {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.erro.set('Criança não informada.');
      this.carregando.set(false);
      return;
    }

    this.childId = Number(id);
    if (Number.isNaN(this.childId) || this.childId <= 0) {
      this.erro.set('Identificador da criança inválido.');
      this.carregando.set(false);
      return;
    }

    await this.carregar();
  }

  async carregar(): Promise<void> {
    this.carregando.set(true);
    this.erro.set('');

    try {
      const [crianca, dispositivos, meus] = await Promise.all([
        firstValueFrom(this.childrenService.get(this.childId)),
        firstValueFrom(this.nfcService.list(this.childId)),
        firstValueFrom(this.devicesService.listMine())
      ]);

      this.crianca.set(crianca);
      this.dispositivos.set(dispositivos);
      this.disponiveis.set(meus.filter(item => item.status === 'CLAIMED'));
    } catch (erro) {
      console.error('Erro ao carregar NFCs:', erro);
      this.erro.set('Não foi possível carregar os dispositivos NFC.');
    } finally {
      this.carregando.set(false);
    }
  }

  async adicionar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.erro.set('');
    this.salvando.set(true);

    try {
      const dados = this.formulario.getRawValue();
      await firstValueFrom(
        this.devicesService.assign(Number(dados.deviceId), {
          personId: this.childId,
          deviceName: dados.deviceName.trim()
        })
      );
      this.formulario.reset({ deviceId: '', deviceName: '' });
      await this.carregar();
    } catch (erro: unknown) {
      console.error('Erro ao vincular NFC:', erro);
      const message = (erro as { error?: { message?: string } })?.error?.message;
      this.erro.set(message ?? 'Não foi possível vincular o dispositivo NFC.');
    } finally {
      this.salvando.set(false);
    }
  }

  async alternar(dispositivo: NfcDevice): Promise<void> {
    this.erro.set('');
    try {
      const atualizado = await firstValueFrom(
        this.nfcService.toggle(this.childId, dispositivo.id)
      );
      this.dispositivos.update(lista =>
        lista.map(item => item.id === atualizado.id ? atualizado : item)
      );
    } catch (erro) {
      console.error('Erro ao alterar NFC:', erro);
      this.erro.set('Não foi possível alterar o status do NFC.');
    }
  }

  async desvincular(dispositivo: NfcDevice): Promise<void> {
    this.erro.set('');
    try {
      await firstValueFrom(this.devicesService.unassign(dispositivo.id));
      await this.carregar();
    } catch (erro) {
      console.error('Erro ao desvincular NFC:', erro);
      this.erro.set('Não foi possível desvincular o dispositivo.');
    }
  }

  categoriaLabel(categoria: NfcDeviceCategory): string {
    return this.categorias.find(item => item.value === categoria)?.label ?? categoria;
  }

  linkNfc(dispositivo: NfcDevice): string {
    return `${window.location.origin}/d/${dispositivo.deviceToken}`;
  }

  async copiar(dispositivo: NfcDevice): Promise<void> {
    const link = this.linkNfc(dispositivo);
    if (!link) {
      return;
    }
    try {
      await navigator.clipboard.writeText(link);
    } catch (erro) {
      console.error('Não foi possível copiar:', erro);
      this.erro.set('Não foi possível copiar o link NFC.');
    }
  }
}
