import {useState, useEffect, useRef} from "react";
import {useHistory, useLocation} from "react-router-dom";
import http from "./http-common";
import {getAdvancedSearchFields, getSearchTerm} from "./SearchTerms";
import StatutesRecurse from "./StatutesRecurse";
import AppBreadcrumb from "./AppBreadcrumb";
import "./Statutes.css";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export default function Statutes(props) {
  const history = useHistory();
  const query = useQuery();
  const [viewModel, setViewModel] = useState();
  const [path, setPath] = useState(query.get('path') === null ? '' : query.get('path'));
  const [term, setTerm] = useState(query.get('term') === null ? '' : query.get('term'));
  const [frag, setFrag] = useState(query.get('frag') === null ? '' : query.get('frag'));
  const [searchTerm, setSearchTerm] = useState(term);
  const fragDisabled = useRef(path === '' || term === '' );
  const [urlParams, setUrlParams] = useState(duplicateFuckingCode());
  const searchTerms = getAdvancedSearchFields(term);
  const [allSearchTerm, setAllSearchTerm] = useState(searchTerms[0]);
  const [notSearchTerm, setNotSearchterm] = useState(searchTerms[1]);
  const [anySearchTerm, setAnySearchTerm] = useState(searchTerms[2]);
  const [exactSearchTerm, setExactSearchTerm] = useState(searchTerms[3]);

  function duplicateFuckingCode() {
    const params = new URLSearchParams();
    fragDisabled.current = ( path === '' || term === '' );
    if ( path !== '' ) params.append('path', path);
    if ( term !== '' ) params.append('term', term);
    if ( !fragDisabled.current && frag !== '' ) params.append('frag', frag);
    return params;
  }

  // Similar to componentDidMount and componentDidUpdate:
  useEffect(() => {
    const params = new URLSearchParams();
    fragDisabled.current = ( path === '' || term === '' );
    if ( path !== '' ) params.append('path', path);
    if ( term !== '' ) params.append('term', term);
    if ( !fragDisabled.current && frag !== '' ) params.append('frag', frag);
    setUrlParams(params);
  },[path, term, frag]);

  useEffect(() => {
    history.push('/statutes?' + urlParams);
    return http.get('api?'+urlParams)
    .then(response => {
      setViewModel(response.data);
    });
  },[history, urlParams]);

  function handleSubmit(event) {
    event.preventDefault();
    setTerm(searchTerm);
//    let params = duplicateFuckingCode();
//    let newTerm = getSearchTerm(allSearchTerm, notSearchTerm, anySearchTerm, exactSearchTerm)
//    if ( params.has('term') || )
//    if ( newTerm !== '' ) params.append('term', newTerm);
//    history.location.push('/statutes?' + params);
  }
  function handleAnySearchTerm(event) {
    setAnySearchTerm(event.target.value);
 }
  function handleFrag(event) {
    setFrag(!frag);
    event.target.blur();
  }
  function handleClear(event) {
    event.target.blur();
    setFrag(false);
    setSearchTerm('');
    setTerm('');
  };
  function navFacet(fullFacet) {
    setPath(fullFacet);
  };

  if ( viewModel != null && viewModel.entries.length > 0 ) {
    return (
      <div className="container">
        <nav className="navbar navbar-expand-lg navbar-light bg-light">
          <a className="navbar-brand" href="/">
            <img src="spring-logo.png" width="95" height="50" className="d-inline-block align-center" alt="" loading="lazy"/>
          </a>
          <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <div className="btn-group" role="group" aria-label="Button group with nested dropdown"></div>
            <form className="navbar-nav mr-auto form-inline my-2 my-lg-0" id="search-form" onSubmit={handleSubmit}>
              <input className="form-control mr-sm-2" placeholder="Search" value={searchTerm} id="search-input" onChange={e=>setSearchTerm(e.target.value)} aria-label="Search" />
              <div className="btn-group" >
              <button className="btn btn-outline-secondary my-2 my-sm-0" id="search-submit">Submit</button>
              <div className="btn-group" role="group">
                <button className="btn btn-outline-secondary dropdown-toggle" data-toggle="dropdown"><span className="caret"></span></button>
              <div className="dropdown-menu">
               <div className="px-4 py-3">
                  <div className="form-group">
                   <label htmlFor="inAll">All&nbsp;Of:&nbsp;&nbsp;</label>
                    <input type="text" className="form-control" name="inAll" id="inAll" />
                  </div>
                  <div className="form-group">
                   <label htmlFor="inNot">None&nbsp;Of:&nbsp;&nbsp;</label>
                    <input type="text" className="form-control" name="inNot" id="inNot" />
                  </div>
                  <div className="form-group">
                    <label htmlFor="inAny">Any&nbsp;Of:&nbsp;&nbsp;</label>
                    <input type="text" className="form-control" name="inAny" id="inAny" value={anySearchTerm.current} onChange={handleAnySearchTerm}/>
                  </div>
                  <div className="form-group">
                    <label htmlFor="inExact">Exact&nbsp;Phrase:&nbsp;&nbsp;</label>
                    <input type="text" className="form-control" name="inExact" id="inExact"/>
                  </div>
                  <button type="submit" className="btn btn-primary" id="search-form-input">Submit</button>
               </div>
              </div>
              </div>
              </div>
              <button className="btn btn-light my-2 my-sm-0" name="cl" id="search-clear" onClick={handleClear}>Clear</button>
              { fragDisabled.current ? 
                  <button className="btn btn-list my-2 my-sm-0" id="search-frag" disabled>Fragments</button>
                  : frag ? 
                    <button className="btn btn-primary my-2 my-sm-0" id="search-frag" onClick={handleFrag}>Fragments</button>
                    : <button className="btn btn-light my-2 my-sm-0" id="search-frag" onClick={handleFrag}>Fragments</button>
              }
              <input type="hidden" name="fs" />
            </form>
            <ul className="navbar-nav ml-auto">
              <li className="nav-item dropdown">
                <a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Applications</a>
                <div className="dropdown-menu" aria-labelledby="navbarDropdown">
                  <a className="dropdown-item" href="/statutes">Guided Search</a>
                  <a className="dropdown-item" href="/">Court Opinions</a>
                </div>
              </li>
            </ul>
          </div>
        </nav>
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb" id="breadcrumbs">
            <li className='breadcrumb-item' onClick={() => navFacet('')} style={{ cursor: 'pointer' }}>Home</li>
            <AppBreadcrumb entries={viewModel.entries} navFacet={navFacet} />
          </ol>
        </nav>
        <StatutesRecurse entries={viewModel.entries} navFacet={navFacet} index={0}/>
      </div>
    );
  }
  return null;
}

