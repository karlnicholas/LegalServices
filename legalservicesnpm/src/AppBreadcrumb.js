import React from "react";

export default function AppBreadcrumb(props) {
  function recurse(entries, navFacet) {
    if ( entries !== null && entries.length === 1) {
      const entry = entries[0];
      if ( entry.entries !== null && entry.entries.length === 1) {
        return (<><li className='breadcrumb-item libreadcrumitemhover' onClick={() => navFacet(entry.fullFacet)} style={{ cursor: 'pointer' }}>{entry.text}</li><AppBreadcrumb entries={entry.entries} navFacet={navFacet}/></>);
      } else if ( entry.entries != null && entry.entries.length > 1) {
        return (<><li className='breadcrumb-item libreadcrumitemhover' onClick={() => navFacet(entry.fullFacet)} style={{ cursor: 'pointer' }}>{entry.text} - {entry.statutesBaseClass.title}</li><AppBreadcrumb entries={entry.entries} navFacet={navFacet}/></>);
      }
    }
  }
  function breadcrumbCount(totalCount, term) {
    if ( term !== undefined) {
      return(<li><span className="badge badge-primary pull-right">{totalCount}</span></li>);
    }
  }
  return (
      <>
      {recurse(props.entries, props.navFacet)}
      {breadcrumbCount(props.totalCount, props.term)}
      </>
  )
}
