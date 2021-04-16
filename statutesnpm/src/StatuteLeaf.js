import React from "react";

export default class StatuteLeaf extends React.Component {
	render() {
		if ( this.props.entry.sectionText ) {
			return (<div className='row'><div className='col-sm-12'>{this.props.entry.text}</div></div>);
		} else {
			return null;
		}
	}
 }
