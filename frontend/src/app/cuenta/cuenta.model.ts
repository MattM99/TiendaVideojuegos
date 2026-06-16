import { PersonaModel } from "../persona/persona.model";

export interface CuentaModel {
  nickname: string;
  rol: string;
  estado: string;
  persona: PersonaModel;

}
