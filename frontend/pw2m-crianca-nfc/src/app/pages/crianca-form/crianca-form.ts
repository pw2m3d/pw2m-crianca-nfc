import {
  Component,
  OnInit
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import { firstValueFrom } from 'rxjs';

import {
  ChildrenService
} from '../../core/services/children.service';

import {
  ChildPayload,
  PersonType
} from '../../core/models/child.models';

@Component({
  selector: 'app-crianca-form',
  standalone: true,

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './crianca-form.html',
  styleUrl: './crianca-form.scss'
})
export class CriancaForm implements OnInit {

  carregando = false;
  carregandoDados = false;

  erro = '';

  id: number | null = null;

  formulario;

  constructor(
    private fb: FormBuilder,
    private childrenService: ChildrenService,
    private router: Router,
    private route: ActivatedRoute
  ) {

    this.formulario =
      this.fb.nonNullable.group({

        fullName: [
          '',
          [
            Validators.required,
            Validators.minLength(3)
          ]
        ],

        personType: this.fb.nonNullable.control<PersonType>('CHILD'),

        birthDate: [''],
        bloodType: [''],
        addressLine: [''],
        city: [''],
        state: [''],

        allergies: [''],
        conditions: [''],
        medications: [''],

        healthPlan: [''],
        healthPlanNumber: [''],

        emergencyNotes: [''],

        contactName: [
          '',
          Validators.required
        ],

        contactRelation: [
          '',
          Validators.required
        ],

        contactPhone: [
          '',
          Validators.required
        ],

        contactWhatsapp: [''],

        publicEnabled: [true]

      });

  }

  async ngOnInit() {

    const id =
      this.route.snapshot.paramMap.get('id');

    if (!id) {
      return;
    }

    this.id = Number(id);

    await this.carregarCrianca();
  }


  get editando(): boolean {
    return this.id !== null;
  }


  async carregarCrianca() {

    if (!this.id) {
      return;
    }

    this.carregandoDados = true;
    this.erro = '';

    try {

      const crianca =
        await firstValueFrom(
          this.childrenService.get(this.id)
        );

      const contato =
        crianca.contacts?.[0];

      this.formulario.patchValue({

        fullName:
          crianca.fullName ?? '',

        personType:
          crianca.personType ?? 'CHILD',

        birthDate:
          crianca.birthDate ?? '',

        bloodType:
          crianca.bloodType ?? '',

        addressLine:
          crianca.addressLine ?? '',

        city:
          crianca.city ?? '',

        state:
          crianca.state ?? '',

        allergies:
          crianca.medical?.allergies ?? '',

        conditions:
          crianca.medical?.conditions ?? '',

        medications:
          crianca.medical?.medications ?? '',

        healthPlan:
          crianca.medical?.healthPlan ?? '',

        healthPlanNumber:
          crianca.medical?.healthPlanNumber ?? '',

        emergencyNotes:
          crianca.medical?.emergencyNotes ?? '',

        contactName:
          contato?.name ?? '',

        contactRelation:
          contato?.relation ?? '',

        contactPhone:
          contato?.phone ?? '',

        contactWhatsapp:
          contato?.whatsapp ?? '',

        publicEnabled:
          crianca.publicEnabled

      });

    } catch (erro: any) {

      console.error(
        'Erro ao carregar criança:',
        erro
      );

      this.erro =
        'Não foi possível carregar a ficha.';

    } finally {

      this.carregandoDados = false;

    }

  }


  async salvar() {

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.erro = '';
    this.carregando = true;

    const dados =
      this.formulario.getRawValue();

    const payload: ChildPayload = {

      fullName:
        dados.fullName,

      personType:
        dados.personType as PersonType,

      birthDate:
        dados.birthDate || null,

      bloodType:
        dados.bloodType || null,

      addressLine:
        dados.addressLine || null,

      city:
        dados.city || null,

      state:
        dados.state
          ? dados.state.toUpperCase()
          : null,

      publicEnabled:
        dados.publicEnabled,

      medical: {

        allergies:
          dados.allergies || null,

        conditions:
          dados.conditions || null,

        medications:
          dados.medications || null,

        healthPlan:
          dados.healthPlan || null,

        healthPlanNumber:
          dados.healthPlanNumber || null,

        emergencyNotes:
          dados.emergencyNotes || null

      },

      contacts: [
        {
          name:
            dados.contactName,

          relation:
            dados.contactRelation,

          phone:
            dados.contactPhone,

          whatsapp:
            dados.contactWhatsapp || null,

          priority: 1
        }
      ]

    };

    try {

      if (this.editando && this.id) {

        await firstValueFrom(
          this.childrenService.update(
            this.id,
            payload
          )
        );

      } else {

        await firstValueFrom(
          this.childrenService.create(
            payload
          )
        );

      }

      await this.router.navigate([
        '/dashboard'
      ]);

    } catch (erro: any) {

      console.error(
        'Erro ao salvar criança:',
        erro
      );

      this.erro =
        erro?.error?.message ??
        'Não foi possível salvar a ficha.';

    } finally {

      this.carregando = false;

    }

  }

}