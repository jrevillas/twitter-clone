import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';
import 'rxjs/add/observable/throw';

@Injectable()
export class CheckoutService {

  constructor (private http: Http) {}

  postToken(token) {
    return this.http.post('http://localhost:8080/stripe', token);
  }

}
