import React from "react";
import http from "./http-common";
import StatutesRecurse from "./StatutesRecurse";
import AppBreadcrumb from "./AppBreadcrumb";

export default class Statutes extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			entries: [], 
			facetpath: '',
			hiddenFrag: false, 
		}
	}
	fetchStatutes(facetpath) {
		// Typical usage (don't forget to compare props):
		return http.get('/api')
		.then(response => {
			this.setState({entries: response.data.entries, facetpath: facetpath});
		});
	}
	
	componentDidUpdate(prevProps, prevState) {
		if ( this.props.match.params.facetpath !== this.state.facetpath ) {
			this.fetchStatutes(this.props.match.params.facetpath);
		} 
	}

	componentDidMount() {
		this.fetchStatutes(this.props.match.params.facetpath);
	}

	render() {
		if ( this.state.entries.length > 0 ) {
		  return (
		      <div className="container">
		      <nav className="navbar navbar-expand-lg navbar-light bg-light">
		        <a className="navbar-brand" href="/">
		          <img src="spring-logo.png" width="95" height="50" className="d-inline-block align-center" alt="" loading="lazy"/>
		        </a>
		        <div className="collapse navbar-collapse" id="navbarSupportedContent">
		        <div className="btn-group" role="group" aria-label="Button group with nested dropdown">
		        </div>
		        <form action="/" className="navbar-nav mr-auto form-inline my-2 my-lg-0" id="search-form" role="form">
		            <input type="hidden" id="hidden-path" value=""/>
		            <input type="hidden" id="hidden-term" value=""/>
		            <input type="hidden" id="hidden-frag" value=""/>
		            <input className="form-control mr-sm-2" placeholder="Search" id="search-input" aria-label="Search"/>
		            <div className="btn-group" >
		            <button className="btn btn-outline-secondary my-2 my-sm-0" id="search-submit">Submit</button>
		            <div className="btn-group" role="group">
		              <button className="btn btn-outline-secondary dropdown-toggle" data-toggle="dropdown"><span className="caret"></span></button>
		            <div className="dropdown-menu">
		             <div className="px-4 py-3">
		                <div className="form-group">
		                 <label for="inAll">All&nbsp;Of:&nbsp;&nbsp;</label>
		                  <input type="text" className="form-control" name="inAll" value="" id="inAll" />
		                </div>
		                <div className="form-group">
		                 <label for="inNot">None&nbsp;Of:&nbsp;&nbsp;</label>
		                  <input type="text" className="form-control" name="inNot" value="" id="inNot" />
		                </div>
		                <div className="form-group">
		                  <label for="inAny">Any&nbsp;Of:&nbsp;&nbsp;</label>
		                  <input type="text" className="form-control" name="inAny" value="" id="inAny" />
		                </div>
		                <div className="form-group">
		                  <label for="inExact">Exact&nbsp;Phrase:&nbsp;&nbsp;</label>
		                  <input type="text" className="form-control" name="inExact" value="" id="inExact" />
		                </div>
		                <button type="submit" className="btn btn-primary" id="search-form-input">Submit</button>
		             </div>
		            </div>
		            </div>
		            </div>
		            <button className="btn btn-light my-2 my-sm-0" name="cl" id="search-clear" onclick="this.blur();">Clear</button>
		            <button className="btn my-2 my-sm-0" id="search-frag" onclick="this.blur();">Fragments</button>
		            <input type="hidden" name="fs" value="false" />
		          </form>

		            <ul className="navbar-nav ml-auto">
		            <li className="nav-item dropdown">
		              <a className="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Applications</a>
		              <div className="dropdown-menu" aria-labelledby="navbarDropdown">
		                <a className="dropdown-item" href="/statutes">Guided Search</a>
		                <a className="dropdown-item" href="/">Court Opinions</a>
		              </div>
		            </li>
		          </ul>
		        </div>
		      </nav>
		      <nav aria-label="breadcrumb"><ol className="breadcrumb" id="breadcrumbs"><AppBreadcrumb entries={this.state.entries} /></ol></nav>
			  <StatutesRecurse entries={this.state.entries} index={0}/>
		      </div>
		  );
		}
		return null;
	}
}
