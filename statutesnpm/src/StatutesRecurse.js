import React from "react";

import StatuteNode from "./StatuteNode";
import StatuteLeaf from "./StatuteLeaf";


export default class StatutesRecurse extends React.Component {
	constructor(props) {
		super(props);
	}
	
	recurse(entries, index, navFacet) {
		if (entries[index].pathPart) {
			return (<StatutesRecurse entries={entries[index].entries} index={0} navFacet={navFacet}/>);
	    } else {
			return (<StatutesRecurse entries={entries} index={index+1} navFacet={navFacet}/>);
	    }
	}
	render() {
		if ( this.props.index < this.props.entries.length ) {
			return ( 
				<>
				<StatuteNode entry={this.props.entries[this.props.index]} navFacet={this.props.navFacet}/>
				<StatuteLeaf entry={this.props.entries[this.props.index]} navFacet={this.props.navFacet}/>
                {this.recurse(this.props.entries, this.props.index, this.props.navFacet)}
				</>
			)
		}
		return null;
	}
 }
