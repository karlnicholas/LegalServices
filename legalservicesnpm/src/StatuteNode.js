import React from "react";

export default function StatuteNode(props) {
  function getCollapse(length, hiddenFrag) {
    if ( length > 0 && hiddenFrag === 'true' ) {
      return <span className='icon' data-toggle='collapse' data-target='#collapse'><svg className='bi bi-chevron-right' width='20' height='20' viewBox='0 0 20 20' fill='currentColor' xmlns='http://www.w3.org/2000/svg'><path fill-rule='evenodd' d='M6.646 3.646a.5.5 0 01.708 0l6 6a.5.5 0 010 .708l-6 6a.5.5 0 01-.708-.708L12.293 10 6.646 4.354a.5.5 0 010-.708z'/></svg></span>;
    }
  }
  function getBadge(count) {
    if ( count > 0 ) {
      return (<span className="badge badge-primary pull-right">{count}</span>);	    	
    }
  }
  function getSection(entry) {
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
  if ( !props.entry.pathPart && !props.entry.sectionText ) {
    return (
        <div className="row rowhover" onClick={() => props.navFacet(props.entry.fullFacet)} style={{ cursor: 'pointer' }}>
          <div className='col-sm-1' style={{ cursor: 'pointer' }}>
          {getCollapse(props.entry.entries.length, props.hiddenFrag)}
          </div>
          <div className="reference col-sm-3">{props.entry.displayTitle}
          {getBadge(props.entry.count)}
          </div>
          <div className="reference col-sm-6">{props.entry.statutesBaseClass.title}</div>
          {getSection(props.entry)}
          </div>
    );
  }
  return null;
}