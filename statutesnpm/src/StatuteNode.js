import React from "react";
import {withRouter} from 'react-router-dom'

class StatuteNode extends React.Component {
	getCollapse(length, hiddenFrag) {
		if ( length > 0 && hiddenFrag === 'true' ) {
			return <span className='icon' data-toggle='collapse' data-target='#collapse'><svg className='bi bi-chevron-right' width='20' height='20' viewBox='0 0 20 20' fill='currentColor' xmlns='http://www.w3.org/2000/svg'><path fill-rule='evenodd' d='M6.646 3.646a.5.5 0 01.708 0l6 6a.5.5 0 010 .708l-6 6a.5.5 0 01-.708-.708L12.293 10 6.646 4.354a.5.5 0 010-.708z'/></svg></span>;
		}
	}
	getBadge(count) {
	    if ( count > 0 ) {
             return (<span className="badge badge-primary pull-right">{count}</span>);	    	
	    }
	}
	getSection(entry) {
	    if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	return (<div className='reference col-sm-2'>§§ {entry.statutesBaseClass.statuteRange.sNumber.sectionNumber} - {entry.statutesBaseClass.statuteRange.eNumber.sectionNumber}</div> );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	return (<div className='reference col-sm-2'>§§ {entry.statutesBaseClass.statuteRange.sNumber.sectionNumber}</div>);
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	return (<div className='reference col-sm-2'>§§ {entry.statutesBaseClass.statuteRange.eNumber.sectionNumber}</div>);
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	return (<div className='reference col-sm-2'></div>);
	    }
	}
	navFacet(fullFacet, e) {
	    e.preventDefault();
        this.props.history.push('/statutes?path='+fullFacet);
	}
	render() {
		if ( !this.props.entry.pathPart && !this.props.entry.sectionText ) {
			return (
				<div className="row" onClick={(e) => this.navFacet(this.props.entry.fullFacet, e)} style={{ cursor: 'pointer' }}>
					<div className='col-sm-1' style={{ cursor: 'pointer' }}>
						{this.getCollapse(this.props.entry.entries.length, this.props.hiddenFrag)}
					</div>
					<div className="reference col-sm-3">{this.props.entry.displayTitle}
						{this.getBadge(this.props.entry.count)}
					</div>
				    <div className="reference col-sm-6">{this.props.entry.statutesBaseClass.title}</div>
			    	{this.getSection(this.props.entry)}
				</div>
			);
		}
		return null;
	}
 }
export default withRouter(StatuteNode);