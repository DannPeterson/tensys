import { Source } from "./source";

export class User {
  public id: number;
  public userId: string;
  public firstName: string;
  public lastName: string;
  public username: string;
  public email: string;
  public lastLoginDate: Date;
  public lastLoginDateDisplay: Date;
  public joinDate: Date;

  public paymentDay: Date;
  public paidUntil: Date;
  public company: string;

  public profileImageUrl: string;
  public active: boolean;
  public notLocked: boolean;
  public role: string;
  public language: string;
  public authorities: [];
  public sources: Source[];

  constructor() {
    this.id = 0;
    this.userId = '';
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.paymentDay = null;
    this.paidUntil = null;
    this.company = '';
    this.profileImageUrl = '';
    this.active = false;
    this.notLocked = false;
    this.role = '';
    this.language = '';
    this.authorities = [];
    this.sources = null;
  }
}
