import { Injectable } from '@angular/core';
import { ViewModel } from './viewmodel';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({providedIn: 'root'})

export class ViewModelService {
	private viewmodelUrl = 'http://localhost:8080';  // URL to web api

	constructor(private http: HttpClient) { }

	/** GET ViewModel from the server */
	getViewModel(facet: string): Observable<ViewModel> {
		let params = new HttpParams().set('path', facet);
		return this.http.get<ViewModel>(this.viewmodelUrl, { params: params })
		.pipe(
	        tap(_ => console.log('fetched viewModel')),
			catchError(this.handleError<ViewModel>('getViewModel'))
		);	
    }
	
	/**
	 * Handle Http operation that failed.
	 * Let the app continue.
	 * @param operation - name of the operation that failed
	 * @param result - optional value to return as the observable result
	 */
	private handleError<T> (operation = 'operation', result?: T) {
	  return (error: any): Observable<T> => {
	
	    // TODO: send the error to remote logging infrastructure
	    console.error(error); // log to console instead
	
	    // TODO: better job of transforming error for user consumption
//	    this.log(`${operation} failed: ${error.message}`);
	
	    // Let the app keep running by returning an empty result.
	    return of(result as T);
	  };
	}
}
