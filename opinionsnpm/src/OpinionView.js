import React from "react";
import './css/opinion.css'
import { BsStarFill } from "react-icons/bs";

export default class OpinionView extends React.Component {
	createImportance(n){
		let r1 = [];
		var i;
		for ( i=0; i<n; ++i ) {
			r1.push(<BsStarFill key={i}/>);
		}
		return r1;
	}
	sectionViews(sections){
		if ( sections != null ) {
			return sections.map((section, index) => {
				return (
			        <div className="openstat srow" key={index}>
			        <span className="casestar">{this.createImportance(section.importance)}</span>
			        <span className="openstat code titlepath">{section.displayTitlePath}</span>
			        <a href={'http://op-opca.b9ad.pro-us-east-1.openshiftapps.com/?path='+section.fullFacet}><span className="openstat code sections">{section.displaySections}</span></a>
					</div>
				)
			});
		} else {
			return null;
		}
	}
	citationDate(c) {
		if ( c.opinionDate != null ) {
			return ' (' + new Date(c.opinionDate).toLocaleDateString('en-US', { year: 'numeric'}) + ')';
		}
	}
	opinionCases(cases){
		if ( cases != null ) {
			return cases.slice(0,10).map((c, index) => {
				return (
		          <div key={index} className="opencase orow">
		          <span className="casestar">{this.createImportance(c.importance)}</span>
		          <span className="opencase title">{c.title}</span>
        		  <span className="opencase citedetails">{this.citationDate(c)} {c.citation}</span>
		          </div>          
				)
			})
		} else {
			return null;
		}
	}
    moreCases(cases) {
		if ( cases.length > 10 ) {
	        return (<div className="opencase casehead"><span className="opencase case">[{cases.length - 10} more cases cited.]</span></div>);
		} else {
			return null;
		}
	}
	disposition(disposition){
		if ( disposition != null ) 
			return (<div>{disposition}</div>);
		else 
			return (<div>Disposition Unknown</div>);
	}
	summary(summary, publicationStatus){
		if ( summary != null ) 
			return (<div>{summary}</div>);
		else if ( publicationStatus != null ) 
			return (<div>{publicationStatus}</div>);
		else 
			return null;
	}
	render() {
		return (
	        <div className="opinion">
		        <div className="ophead">
		        <span className="ophead date">{new Date(this.props.opinion.opinionDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}</span>
		        <span className="ophead title">{this.props.opinion.title}</span>
		        <a className="ophead right" href={'http://www.courts.ca.gov/opinions/documents/' + this.props.opinion.fileName + '.PDF'}>{this.props.opinion.fileName}</a>
		        </div>
		        <div className="openstat wrap">
				{this.sectionViews(this.props.opinion.sectionViews)}
				</div>
				<div className="opencase wrap"><div className="opencase casehead"><span className="opencase case">Cases Cited:</span></div>
				<div className="opencase casewrap">
				{this.opinionCases(this.props.opinion.cases)}
		        </div>
	        	{this.moreCases(this.props.opinion.cases)}
				</div>
				<div className="summary wrap"><div className="summary outer"><div className="summary inner">
				<span><b>{this.disposition(this.props.opinion.disposition)}</b></span>
				{this.summary(this.props.opinion.summary, this.props.opinion.publicationStatus)}
				</div></div></div>
				<div className="opinion tail" />
			</div>
		);
	}
 }
