import React from "react";

export default class AppBreadcrumb extends React.Component {
	recurse(entries, navFacet) {
		if ( entries !== null && entries.length === 1) {
			const entry = entries[0];
			if ( entry.entries !== null && entry.entries.length === 1) {
                return (<><li className='breadcrumb-item' onClick={() => navFacet(entry.fullFacet)} style={{ cursor: 'pointer' }}>{entry.text}</li><AppBreadcrumb entries={entry.entries} navFacet={navFacet}/></>);
			} else if ( entry.entries != null && entry.entries.length > 1) {
                return (<><li className='breadcrumb-item' onClick={() => navFacet(entry.fullFacet)} style={{ cursor: 'pointer' }}>{entry.text} - {entry.statutesBaseClass.title}</li><AppBreadcrumb entries={entry.entries} navFacet={navFacet}/></>);
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
		{this.recurse(this.props.entries, this.props.navFacet)}
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