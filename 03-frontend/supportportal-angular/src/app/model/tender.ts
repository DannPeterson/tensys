import { Cpv } from "./cpv";
import { Source } from "./source";

export class Tender {
    public id: number;
    public source: Source;
    public sourceRefNumber: string;
    public link: string;
    public title: string;
    public description: string;
    public field: string;
    public client: string;
    public cpv: Cpv[];
    public type: string;
    public date: Date;
    public deadline: Date;

    constructor() {
        this.id = 0;
        this.source = null;
        this.sourceRefNumber = '';
        this.link = '';
        this.title = '';
        this.description = '';
        this.field = '';
        this.client = '';
        this.cpv = null;
        this.type = '';
        this.date = null;
        this.deadline = null;
    }
}