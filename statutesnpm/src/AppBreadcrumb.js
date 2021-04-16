import React from "react";


export default class AppBreadcrumb extends React.Component {
	recurse(entries) {
		if ( entries !== null && entries.length === 1) {
			const entry = entries[0];
			if ( entry.entries !== null && entry.entries.length === 1) {
				return (<><li className='breadcrumb-item' style={{ cursor: 'pointer' }}>{entry.text}</li><AppBreadcrumb entries={entry.entries}/></>);
			} else if ( entry.entries != null && entry.entries.length > 1) {
				return (<><li className='breadcrumb-item' style={{ cursor: 'pointer' }}>{entry.text} - {entry.statutesBaseClass.title}</li><AppBreadcrumb entries={entry.entries}/></>);
			}
		}
	}
	breadcrumbCount(totalCount, term) {
		if ( term !== undefined) {
		  return(<li><span className="badge badge-primary pull-right">{totalCount}</span></li>);
		}
	}
	render() {
		return (
		<>
		{this.recurse(this.props.entries)}
		{this.breadcrumbCount(this.props.totalCount, this.props.term)}
		</>
		)
	}
}

//function breadcrumbs(entries, lis) {
//	if ( entries != null && entries.length == 1) {
//		var entry = entries[0];
//		if ( entry.entries != null && entry.entries.length == 1) {
//			lis.push("<li class='breadcrumb-item' id='" + entry.fullFacet + "' style='cursor:pointer;' >" + entry.text + "</li>");
//		} else if ( entry.entries != null && entry.entries.length > 1) {
//			lis.push("<li class='breadcrumb-item' id='" + entry.fullFacet + "' style='cursor:pointer;' >" + entry.text + " - " + entry.statutesBaseClass.title + "</a></li>");
//		}
//		 breadcrumbs(entry.entries, lis);
//	}
//}
//<AppBreadcrumb entries={entry.entries}/>