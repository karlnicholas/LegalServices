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
		var r = [];
		if ( sections != null ) {
			r.push(sections.map((section, index) => {
				return (
			        <div className="openstat srow" key={index}>
			        <span className="casestar">{this.createImportance(section.importance)}</span>
			        <span className="openstat code titlepath">{section.displayTitlePath}</span>
			        <span className="openstat code sections">{section.displaySections}</span>
					</div>
				)
			}));
		}
		return r;
	}
	opinionCases(cases){
		var r = [];
		if ( cases != null ) {
			r.push(cases.slice(0,10).map((c, index) => {
				return (
		          <div key={index} className="opencase orow">
		          <span className="casestar">{this.createImportance(c.importance)}</span>
		          <span className="opencase title">{c.title}</span>
		          <span className="opencase citedetails">{new Date(c.opinionDate).toLocaleDateString()}</span>
		          </div>          
				)
			}))
		}
		return r;
	}
    moreCases(cases) {
		if ( cases.length > 10 ) {
	        return (<div className="opencase casehead"><span className="opencase case">[{cases.length - 10} more cases cited.]</span></div>);
		} else {
			return null;
		}
	}
	disposition(disposition){
		if ( disposition != null ) return (<div>{disposition}</div>);
		else return (<div>Disposition Unknown</div>);
	}
	summary(summary, publicationStatus){
		if ( summary != null ) return (<div>{summary}</div>);
		else if ( publicationStatus != null ) return (<div>{publicationStatus}</div>);
		else return null;
	}
	render() {
		return (
	        <div className="opinion">
		        <div className="ophead">
		        <span className="ophead date">{new Date(this.props.opinion.opinionDate).toLocaleDateString()}</span>
		        <span className="ophead title">{this.props.opinion.title}</span>
		        <span className="ophead title">{this.props.opinion.name}</span>{this.props.opinion.fileName}
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
