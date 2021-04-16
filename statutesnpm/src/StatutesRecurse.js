import React from "react";

import StatuteNode from "./StatuteNode";
import StatuteLeaf from "./StatuteLeaf";


export default class StatutesRecurse extends React.Component {
	recurse(entries, index) {
		if (entries[index].pathPart) {
			return (<StatutesRecurse entries={entries[index].entries} index={0} />);
	    } else {
			return (<StatutesRecurse entries={entries} index={index+1} />);
	    }
	}
	render() {
		if ( this.props.index < this.props.entries.length ) {
			return ( 
				<div>
				<StatuteNode entry={this.props.entries[this.props.index]}/>
				<StatuteLeaf entry={this.props.entries[this.props.index]}/>
                {this.recurse(this.props.entries, this.props.index)}
				</div>
			)
		} else {
			return null;
		}
	}
 }
