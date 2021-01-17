import React from "react";
import Nbsp from "./nbsp";

export default class OpinionView extends React.Component {
	createImportance(n){
		let r1 = Array(n).fill("*");
		var i;
		for( i=0; i < 4-n; ++i ) {
			r1.push(<Nbsp key={i}/>)
		}
		return r1;
	}
	sectionViews(sections){
		var r = [];
		if ( sections != null ) {
			r.push(sections.map((section, index) => {
				return (
					<div key={index}>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{this.createImportance(section.importance)}</span>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{section.displayTitlePath}</span>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{section.displaySections}</span>
					</div>
				)
			}))
		}
		return r;
	}
	opinionCases(cases){
		var r = [];
		if ( cases != null ) {
			r.push(cases.slice(0,10).map((c, index) => {
				return (
					<div key={index}>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{this.createImportance(c.importance)}</span>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{c.title}</span>
					&nbsp;&nbsp;&nbsp;&nbsp;<span>{new Date(c.opinionDate).toLocaleDateString()}</span>
					</div>
				)
			}))
			if ( cases.length > 10 ) {
				r.push(<div key="10">&nbsp;&nbsp;&nbsp;&nbsp;{cases.length - 10} more cases</div>);
			}
		}
		return r;
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
		console.log(this.props);
		return (
			<div>
			<div>
			<span>{new Date(this.props.opinion.opinionDate).toLocaleDateString()}</span>
			<span>{this.props.opinion.name}</span>
			<span>{this.props.opinion.title}</span>
			<span>{this.props.opinion.fileName}.PDF</span>
			</div>
			{this.sectionViews(this.props.opinion.sectionViews)}
			{this.opinionCases(this.props.opinion.cases)}
			{this.disposition(this.props.opinion.disposition)}
			{this.summary(this.props.opinion.summary, this.props.opinion.publicationStatus)}
			</div>
		);
	}
 }
