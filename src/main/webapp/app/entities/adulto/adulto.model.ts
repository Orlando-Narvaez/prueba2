import dayjs from 'dayjs/esm';
import { INinio } from 'app/entities/ninio/ninio.model';

export interface IAdulto {
  id?: number;
  nombre?: string;
  apellido?: string;
  edad?: number | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  ninios?: INinio[] | null;
}

export class Adulto implements IAdulto {
  constructor(
    public id?: number,
    public nombre?: string,
    public apellido?: string,
    public edad?: number | null,
    public fechaNacimiento?: dayjs.Dayjs | null,
    public ninios?: INinio[] | null
  ) {}
}

export function getAdultoIdentifier(adulto: IAdulto): number | undefined {
  return adulto.id;
}
