import { Injectable } from '@angular/core';
import { StatuteRoot } from './statute-root';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class StatuteRootService {

constructor(
  private http: HttpClient) { }
  private statuterootsUrl = 'http://localhost:8080/statutes';  // URL to web api

	/** GET StatuteRoot from the server */
/*
	getStatuteRoots (): Observable<StatuteRoot[]> {
	  return this.http.get<StatuteRoot[]>(this.statuterootsUrl)
	    .pipe(
//	      tap(_ => this.log('fetched statuteroots')),
	      catchError(this.handleError<StatuteRoot[]>('getStatuteRoots', []))
	    );
	}
*/
	getStatuteRoots (): Observable<StatuteRoot[]> {
	  return this.http.get<StatuteRoot[]>(this.statuterootsUrl)
	    .pipe(
//	      tap(_ => this.log('fetched statuteroots')),
	      catchError(this.handleError<StatuteRoot[]>('getStatuteRoots', []))
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
