import React from "react";
import { BsStarFill } from "react-icons/bs";

export default function OpinionView(props) {
  function createImportance(n){
    let r1 = [];
    var i;
    for ( i=0; i<n; ++i ) {
      r1.push(<BsStarFill key={i}/>);
    }
    return r1;
  }
  function sectionViews(sections){
    if ( sections != null ) {
      return sections.map((section, index) => {
        return (
          <div className="openstat srow" key={index}>
          <span className="casestar">{createImportance(section.importance)}</span>
          <span className="openstat code titlepath">{section.displayTitlePath}</span>
          <a href={'statutes?path='+section.fullFacet} ><span className="openstat code sections" style={{ cursor: 'pointer' }}>{section.displaySections}</span></a>
          </div>
        )
      });
    } else {
      return null;
    }
  }
  function citationDate(c) {
    if ( c.opinionDate != null ) {
      return ' (' + new Date(c.opinionDate).toLocaleDateString('en-US', { year: 'numeric', timeZone: 'UTC'}) + ')';
    }
  }
  function opinionCases(cases){
    if ( cases != null ) {
      return cases.slice(0,10).map((c, index) => {
        return (
          <div key={index} className="opencase orow">
          <span className="casestar">{createImportance(c.importance)}</span>
          <span className="opencase title">{c.title}</span>
          <span className="opencase citedetails">{citationDate(c)} {c.citation}</span>
          </div>          
        )
      })
    } else {
      return null;
    }
  }
  function moreCases(cases) {
    if ( cases.length > 10 ) {
          return (<div className="opencase casehead"><span className="opencase case">[{cases.length - 10} more cases cited.]</span></div>);
    } else {
      return null;
    }
  }
  function disposition(disposition){
    if ( disposition != null ) 
      return (<div>{disposition}</div>);
    else 
      return (<div>Disposition Unknown</div>);
  }
  function summary(summary, publicationStatus){
    if ( summary != null ) 
      return (<div>{summary}</div>);
    else if ( publicationStatus != null ) 
      return (<div>{publicationStatus}</div>);
    else 
      return null;
  }
  return (
    <div className="opinion">
      <div className="ophead">
        <span className="ophead date">{new Date(props.opinion.opinionDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', timeZone: 'UTC' })}</span>
        <span className="ophead title">{props.opinion.title}</span>
        <a className="ophead right" href={'http://www.courts.ca.gov/opinions/documents/' + props.opinion.fileName + '.PDF'}>{props.opinion.fileName}</a>
      </div>
      <div className="openstat wrap">
        {sectionViews(props.opinion.sectionViews)}
      </div>
      <div className="opencase wrap">
        <div className="opencase casehead"><span className="opencase case">Cases Cited:</span></div>
        <div className="opencase casewrap">
          {opinionCases(props.opinion.cases)}
        </div>
        {moreCases(props.opinion.cases)}
      </div>
      <div className="summary wrap"><div className="summary outer"><div className="summary inner">
        <span><b>{disposition(props.opinion.disposition)}</b></span>
        {summary(props.opinion.summary, props.opinion.publicationStatus)}
      </div></div></div>
      <div className="opinion tail" />
    </div>
  );
}
