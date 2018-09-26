package dalserver.sia;
/**
 * Raw data for the SIAP and Image data models (this class is autogenerated).
 * See {@link dalserver.sia.SiapKeywordFactory}.
 */
public class SiapV1KeywordData {
  /** CSV data for the SIAP and Image data models. */
  public static final String[] data = {
  "Field ID,Type,UTYPE,UCD,Description,FITS,CSV,DataType,ArraySize,Unit,SiapUnit,Hint,Default",
  "## ACCESS Model,,,,,,,,,,,,",
  ",Query,Query,,Query Metadata,,,,,,,Q,",
  "query_score,Double,Query.Score,,Degree of match to query parameters,,,float,,,,Q,",
  "query_token,String,Query.Token,,Continuation token for large queries,,,char,*,,,Qp,",
  ",,,,,,,,,,,,",
  ",Association,Association,,Association Metadata,,,,,,,Q,",
  "assoc_type,String,Association.Type,,Type of association,,,char,*,,,Qp,",
  "assoc_id,String,Association.ID,,Association identifier,,,char,*,,,mQ,",
  "assoc_key,String,Association.Key,,Key used to distinguish association elements,,,char,*,,,Q,",
  ",,,,,,,,,,,,",
  ",Access,Access,,Access Metadata,,,,,,,Q,",
  "access_url,URL,Access.Reference,VOX:Image_AccessReference,URL used to access dataset,,,char,*,,,mQ,",
  "access_format,String,Access.Format,VOX:Image_Format,Content or MIME type of dataset,,9;@ID,char,*,,,mQ,",
  "access_estsize,Int,Access.Size,VOX:Image_FileSize,Estimated dataset size,,10;@ID,int,,,bytes,qQ,",
  "access_ttl,Long,Access.TimeToLive,VOX:Image_AccessRefTTL,Minimum time to live of staged dataset,,,int,,,seconds,Q,",
  ",,,,,,,,,,,,",
  ",,,,,,,,,,,,",
  ",Dataset,Dataset,,General Dataset Metadata,,,,,,,,",
  "datamodel_name,String,Dataset.DataModel.Name,meta.id,Data model name and version,VOCLASS,,char,*,,,qp,Image-1.0",
  "datamodel_prefix,String,Dataset.DataModel.Prefix,meta.id,Data model prefix,,,char,*,,,p,im",
  "datamodel_url,String,Dataset.DataModel.URL,meta.ref.url,Reference URL for data model,,,char,*,,,p,",
  "dataproduct_type,String,Dataset.Type,,Dataset type,,,char,*,,,,",
  "dataproduct_subtype,String,Dataset.Subtype,,Dataset subtype (external type),,,char,*,,,,",
  "calib_level,Long,Dataset.CalibLevel,,Calibration level,,,long,,,,,",
  "dataset_length,Long,Dataset.Length,meta.number,Number of pixels,,,long,,,,,",
  "dataset_deleted,String,Dataset.Deleted,,Set if dataset is deleted,,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",Image,Image,,Image-specific Dataset metadata,,,,,,,,",
  "im_nsubarrays,Int,Image.Nsubarrays,meta.number,Number of image subarrays,,,int,,,,,",
  "im_naxes,Int,Image.Naxes,VOX:Image_Naxes,Number of image axes,,4;@ID,int,,,,m,",
  "im_naxis,Int,Image.Naxis,VOX:Image_Naxis,Length of each axis of each subarray,,5;@ID,int,*,,,m,",
  "im_pixtype,String,Image.Pixtype,,Pixel datatype,,,char,*,,,,",
  "im_pixflags,String,Image.Pixflags,VOX:Image_PixFlags,Pixel processing indicator,,,char,*,,,q,",
  "im_wcsaxes,String,Image.WCSAxes,,WCS axis coordinate types,,7;@ID,char,*,,,,",
  "im_scale,Double,Image.Scale,VOX:Image_Scale,Image spatial scale in arcsec per pixel,,6;@ID,double,*,deg,deg,m,",
  "im_dataref,String,Image.DataRef,meta.ref.url,Access reference URL for Data element metadata,,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",WCS,WCS,,Image WCS,,,,,,,,",
  "xs_frame,String,Data.Mapping.SpatialAxis.CoordFrame,VOX:STC_CoordRefFrame,Coordinate system reference frame,RADECSYS,,char,*,,,q,",
  "xs_equinox,Double,Data.Mapping.SpatialAxis.Equinox,VOX:STC_CoordEquinox,Coordinate system equinox,EQUINOX,,double,,,y,q,",
  "xs_algo,String,Data.Mapping.SpatialAxis.Algorithm,VOX:WCS_CoordProjection,Celestial projection as for FITS WCS,CTYPE,,char,3,,,q,",
  "map_refpixel,Double,Data.Mapping.RefPixel,VOX:WCS_CoordRefPixel,Pixel coordinates of reference point in image,CRPIX,,double,*,,pixels,q,",
  "map_refvalue,Double,Data.Mapping.RefValue,VOX:WCS_CoordRefValue,World coordinates of reference point in image,CRVAL,,double,*,,,q,",
  "map_cdmatrix,Double,Data.Mapping.CDMatrix,VOX:WCS_CDMatrix,WCS CD matrix as for FITS WCS,CD,,double,*,,,q,",
  ",,,,,,,,,,,,",
  ",DataID,DataID,,Dataset Identification Metadata,,,,,,,,",
  "obs_title,String,DataID.Title,VOX:Image_Title,Dataset Title,TITLE,1;@ID,char,*,,,m,",
  "obs_creator_name,String,DataID.Creator,meta.curation,Dataset creator,AUTHOR,,char,*,,,,",
  "obs_collection,String,DataID.Collection,,Data collection to which dataset belongs,COLLECT,11;@ID,char,*,,,,",
  "obs_id,,DataID.ObservationID,,Unique observation ID shared by all obs datasets,,,char,*,,,,",
  "obs_dataset_did,URI,DataID.DatasetDID,meta.id;meta.dataset,IVOA Dataset ID,DS_IDENT,,char,*,,,,",
  "obs_creator_did,URI,DataID.CreatorDID,meta.id,Creator's ID for the dataset,CR_IDENT,,char,*,,,,",
  "obs_creation_date,String,DataID.Date,time.epoch;meta.dataset,Data processing/creation date,DATE,,char,*,,,,",
  "obs_version,String,DataID.Version,meta.version;meta.dataset,Version of dataset,VERSION,,char,*,,,,",
  "obs_creation_type,String,DataID.CreationType,,Dataset creation type,CRETYPE,,char,*,,,,",
  "obs_logo,URL,DataID.Logo,meta.ref.url,URL for creator logo,VOLOGO,,char,*,,,,",
  "obs_contributor,String,DataID.Contributor,,Contributor,CONTRIB,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",Provenance,Provenance,,Provenance Metadata,,,,,,,,",
  "facility_name,,Provenance.ObsConfig.Facility.name,,,,,,,,,,",
  "instrument_name,String,Provenance.ObsConfig.Instrument.name,INST_ID,Instrument name,INSTRUME,,char,*,,,q,",
  "obs_bandpass,String,Provenance.ObsConfig.Bandpass,VOX:BandPass_ID,Spectral bandpass name or names,SPECBAND,,char,*,,,q,",
  "obs_datasource,String,Provenance.ObsConfig.DataSource,,Original source of the data,DSSOURCE,,char,*,,,,",
  "proposal_id,String,Provenance.Proposal.Identifier,,Proposal if any dataset is associated with,,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",Curation,Curation,,Curation Metadata,,,,,,,,",
  "publisher,String,Curation.Publisher,meta.curation,Dataset publisher,VOPUB,,char,*,,,,",
  "publisher_id,String,Curation.PublisherID,meta.ref.url;meta.curation,URI for VO Publisher,VOPUBID,,char,*,,,,",
  "obs_publisher_did,URI,Curation.PublisherDID,meta.ref.url;meta.curation,Publisher's ID for the dataset ID,DS_IDPUB,12;@ID,char,*,,,,",
  "obs_release_date,String,Curation.ReleaseDate,,Dataset release date,VODATE,,char,*,,,,",
  "preview,String,Curation.Preview,meta.ref.url,URL of preview graphic for dataset,,,char,*,,,,",
  "version,String,Curation.Version,meta.version;meta.curation,Publisher's version of the dataset,VOVER,,char,*,,,,",
  "data_rights,String,Curation.Rights,meta.code,Restrictions on data access,VORIGHTS,,char,*,,,,",
  "bib_reference,String,Curation.Reference,meta.bib.bibcode,URL or Bibcode for documentation,VOREF,,char,*,,,,",
  "contact_name,String,Curation.Contact.Name,meta.bib.author;meta.curation,Contact name,CONTACT,,char,*,,,,",
  "contact_email,String,Curation.Contact.Email,meta.ref.url;meta.email,Contact email,EMAIL,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",Target,Target,,Target Metadata,,,,,,,,",
  "target_name,String,Target.Name,meta.id;src,Target name,OBJECT,,char,*,,,,",
  "target_desc,String,Target.Description,meta.note;src,Target description,OBJDESC,,char,*,,,,",
  "target_class,String,Target.Class,src.class,Object class of observed target,SRCCLASS,,char,*,,,,",
  "target_spectralclass,String,Target.SpectralClass,src.spType,Object spectral class,SPECTYPE,,char,*,,,,",
  "target_pos,Double,Target.Pos,pos.eq;src,Target RA and Dec,RA_TARG DEC_TARG,,double,2,deg,deg,,",
  "target_redshift,Double,Target.Redshift,src.redshift,Target redshift,REDSHIFT,,double,,,,,",
  "target_varampl,Double,Target.VarAmpl,src.var.amplitude,Target variability amplitude (typical),TARGVAR,,double,,,,,",
  ",,,,,,,,,,,,",
  ",Derived,Derived,,Derived Metadata,,,,,,,,",
  "derived_snr,Double,Derived.SNR,stat.snr,Signal-to-noise for observed target,DER_SNR,,double,,,,,",
  "derived_redshift,Double,Derived.Redshift.Value,src.redshift,Measured redshift for target,DER_Z,,double,,,,,",
  "derived_staterr,Double,Derived.Redshift.StatError,stat.error;src.redshift,Error on measured redshift,DER_ZERR,,double,,,,,",
  "derived_redshiftconf,Double,Derived.Redshift.Confidence,,Confidence value on redshift,DER_CONF,,double,,,,,",
  "derived_varampl,Double,Derived.VarAmpl,src.var.amplitude;arith.ratio,Variability amplitude as fraction of mean,DER_VAR,,double,,,,,",
  ",,,,,,,,,,,,",
  ",CoordSys,CoordSys,,Coordinate System Metadata,,,,,,,,",
  "coordsys_id,String,CoordSys.ID,,ID string for coordinate system,VOCSID,,char,*,,,p,",
  ",SpaceFrame,CoordSys.SpaceFrame,,Spatial frame metadata,,,,,,,,",
  "sf_id,String,CoordSys.SpaceFrame.ID,meta.id,ID string for spatial frame,,,char,*,,,p,",
  "sf_name,String,CoordSys.SpaceFrame.Name,pos.frame,Spatial coordinate frame name,RADECSYS,,char,*,,,p,",
  "sf_ucd,String,CoordSys.SpaceFrame.UCD,meta.ucd,Space frame UCD,,,char,*,,,p,",
  "sf_refpos,String,CoordSys.SpaceFrame.RefPos,,Origin of SpaceFrame,,,char,*,,,p,",
  "sf_equinix,Double,CoordSys.SpaceFrame.Equinox,time.equinox;pos.frame,Equinox,EQUINOX,,double,,y,y,p,",
  ",TimeFrame,CoordSys.TimeFrame,,Time frame metadata,,,,,,,,",
  "tf_id,String,CoordSys.TimeFrame.ID,meta.id,ID string for time frame,,,char,*,,,p,",
  "tf_name,String,CoordSys.TimeFrame.Name,time.scale,Timescale,TIMESYS,,char,*,,,p,",
  "tf_ucd,String,CoordSys.TimeFrame.UCD,meta.ucd,Time frame UCD,,,char,*,,,p,",
  "tf_refpos,String,CoordSys.TimeFrame.RefPos,time.scale,Location for times of photon arrival,,,char,*,,,p,",
  "tf_zero,Double,CoordSys.TimeFrame.Zero,arith.zp;time,Zero point of timescale in MJD,MJDREF,,double,,d,,p,",
  ",SpectralFrame,CoordSys.SpectralFrame,,Spectral frame metadata,,,,,,,,",
  "ef_id,String,CoordSys.SpectralFrame.ID,meta.id,ID string for spectral frame,,,char,*,,,p,",
  "ef_name,String,CoordSys.SpectralFrame.Name,,Spectral frame name,,,char,*,,,p,",
  "ef_ucd,String,CoordSys.SpectralFrame.UCD,meta.ucd,Spectral frame UCD,,,char,*,,,p,",
  "ef_refpos,String,CoordSys.SpectralFrame.RefPos,spect.frame,Spectral frame origin,SPECSYS,,char,*,,,p,",
  "ef_redshift,Double,CoordSys.SpectralFrame.Redshift,,Redshift value used if restframe corrected,,,double,,,,p,",
  ",RedshiftFrame,CoordSys.RedshiftFrame,,Redshift frame metadata,,,,,,,,",
  "rf_id,String,CoordSys.RedshiftFrame.ID,meta.id,ID string for redshift frame,,,char,*,,,p,",
  "rf_name,String,CoordSys.RedshiftFrame.Name,,Redshift frame name,,,char,*,,,p,",
  "rf_ucd,String,CoordSys.RedshiftFrame.UCD,meta.ucd,Redshift frame UCD,,,char,*,,,p,",
  "rf_refpos,String,CoordSys.RedshiftFrame.RefPos,,Redshift frame origin,,,char,*,,,p,",
  "rf_doppler,String,CoordSys.RedshiftFrame.DopplerDefinition,,Type of redshift,,,char,*,,,p,",
  ",FluxFrame,CoordSys.FluxFrame,,Flux (observable) frame metadata,,,,,,,,",
  "ff_id,String,CoordSys.FluxFrame.ID,meta.id,ID string for flux frame,,,char,*,,,p,",
  "ff_name,String,CoordSys.FluxFrame.Name,,Name of photometric band,,,char,*,,,p,",
  "ff_ucd,String,CoordSys.FluxFrame.UCD,meta.ucd,UCD for photometric calibration,,,char,*,,,p,",
  "ff_refid,String,CoordSys.FluxFrame.refID,meta.ref.ivorn,URI for photometric calibration,,,char,*,,,p,",
  ",,,,,,,,,,,,",
  ",SpatialAxis,Char.SpatialAxis,,Spatial Axis Characterization,,,,,,,,",
  "s_name,String,Char.SpatialAxis.Name,meta.id,Name for spatial axis,,,char,*,,,,",
  "s_ucd,String,Char.SpatialAxis.UCD,meta.ucd,UCD for spatial coord,SKY_UCD,,char,*,,,,",
  "s_unit,String,Char.SpatialAxis.Unit,meta.unit,Unit for spatial coord,,,char,*,,deg,,",
  "s_ra,Double,Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1,POS_EQ_RA_MAIN,Spatial position first coordinate,,2;@ID,double,,deg,deg,m,",
  "s_dec,Double,Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2,POS_EQ_DEC_MAIN,Spatial position second coordinate,,3;@ID,double,,deg,deg,m,",
  "s_fov,Double,Char.SpatialAxis.Coverage.Bounds.Extent.Diameter,pos.AngSize;instr.fov,Diameter of field of view,,,double,,deg,deg,,",
  "s_lo_ra,Double,Char.SpatialAxis.Coverage.Bounds.Limits.LoLimit2Vec.C1,pos.eq.ra;stat.min,Lower bounds of image spatial coordinates,,,double,2,deg,deg,,",
  "s_lo_dec,Double,Char.SpatialAxis.Coverage.Bounds.Limits.LoLimit2Vec.C2,pos.eq.dec;stat.min,Lower bounds of image spatial coordinates,,,double,2,deg,deg,,",
  "s_hi_ra,Double,Char.SpatialAxis.Coverage.Bounds.Limits.HiLimit2Vec.C1,pos.eq.ra;stat.max,Higher bounds of image spatial coordinates,,,double,2,deg,deg,,",
  "s_hi_dec,Double,Char.SpatialAxis.Coverage.Bounds.Limits.HiLimit2Vec.C2,pos.eq.dec;stat.max,Higher bounds of image spatial coordinates,,,double,2,deg,deg,,",
  "s_region,AstroCoordArea,Char.SpatialAxis.Coverage.Support.Area,,Aperture region,REGION,,char,*,deg,deg,,",
  "s_area,,Char.SpatialAxis.Coverage.Support.Extent,pos.AngSize;instr.fov,Filled area of aperture region,AREA,,double,,sqdeg,sqdeg,,",
  "s_extent,Double,Char.SpatialAxis.Sampling.SampleExtent,phys.angSize;instr.pixel,Spatial bin size,,,float,,,,,",
  "s_fillfactor,Double,Char.SpatialAxis.Sampling.RefVal.FillFactor,stat.filling;pos,Spatial sampling filling factor,SKY_FILL,,float,,,,,",
  "s_stat_error,Double,Char.SpatialAxis.Accuracy.StatError.RefVal.Value,stat.error;pos.eq,Astrometric statistical error,SKY_ERR,,double,,deg,deg,,",
  "s_sys_error,Double,Char.SpatialAxis.Accuracy.SysError.RefVal.Value,stat.error.sys;pos.eq,Astrometric systematic error,SKY_SYE,,double,,deg,deg,,",
  "s_calib_status,String,Char.SpatialAxis.CalibrationStatus,meta.code.qual,Type of spatial coord calibration,SKY_CAL,,char,*,,,,",
  "s_resolution,Double,Char.SpatialAxis.Resolution.Refval.Value,pos.angResolution,Spatial resolution of data,SKY_RES,,double,,arcsec,arcsec,,",
  "s_resolution_min,,Char.SpatialAxis.Resolution.Bounds.Limits.LoLimit,pos.angResolution;stat.min,Lower limit of spatial resolution,,,double,,arcsec,arcsec,,",
  "s_resolution_max,,Char.SpatialAxis.Resolution.Bounds.Limits.HiLimit,pos.angResolution;stat.max,Upper limit of spatial resolution,,,double,,arcsec,arcsec,,",
  ",,,,,,,,,,,,",
  ",SpectralAxis,Char.SpectralAxis,,Spectral Axis Characterization,,,,,,,,",
  "em_name,String,Char.SpectralAxis.Name,meta.id,Name for spectral axis,,,char,*,,,,",
  "em_ucd,String,Char.SpectralAxis.UCD,meta.ucd,UCD for spectral coord,,,char,*,,,,",
  "em_unit,String,Char.SpectralAxis.Unit,VOX:BandPass_Unit,Unit for spectral coord,,,char,*,,m,q,",
  "em_bandpass,Double,Char.SpectralAxis.Coverage.Location.Coord.Spectral.Value,VOX:BandPass_RefValue,Spectral coord value,SPEC_VAL,,double,,,m,q,",
  "em_bandwidth,Double,Char.SpectralAxis.Coverage.Bounds.Extent,em.wl;instr.bandwidth,Bounds of spectral coverage,SPEC_BW,,double,,,m,,",
  "em_min,Double,Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit,VOX:BandPass_LoLimit,Start in spectral coordinate,TDMIN,,double,,,m,q,",
  "em_max,Double,Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit,VOX:BandPass_HiLimit,Stop in spectral coordinate,TDMAX,,double,,,m,q,",
  "em_filled_extent,Double,Char.SpectralAxis.Coverage.Support.Extent,em.wl;instr.bandwidth,Width of filled area of spectral coverage,,,double,,,m,,",
  "em_binsize,Double,Char.SpectralAxis.Sampling.SampleExtent,em.wl;spect.binSize,Wavelength bin size,,,double,,,m,,",
  "em_fill_factor,Double,Char.SpectralAxis.Sampling.RefVal.FillFactor,stat.filling;em,Spectral sampling filling factor,SPEC_FIL,,double,,,,,",
  "em_stat_error,Double,Char.SpectralAxis.Accuracy.StatError.Refval.value,stat.error;em,Spectral coord statistical error,SPEC_ERR,,double,,,m,,",
  "em_sys_error,Double,Char.SpectralAxis.Accuracy.SysError.Refval.value,stat.error.sys;em,Spectral coord systematic error,SPEC_SYE,,double,,,m,,",
  "em_calib_status,String,Char.SpectralAxis.CalibrationStatus,meta.code.qual,Type of spectral coord calibration,SPEC_CAL,,char,*,,m,,",
  "em_resolution,Double,Char.SpectralAxis.Resolution.RefVal.value,spect.resolution;em.wl,Spectral resolution FWHM,SPEC_RES,,double,,,m,,",
  "em_res_power,Double,Char.SpectralAxis.Resolution.ResolPower.RefVal,spect.resolution,Spectral resolving power,SPEC_RP,,double,,,,,",
  "em_res_power_min,Double,Char.SpectralAxis.Resolution.ResolPower.LoLimit,spect.resolution,Low bound of spectral resolving power,,,double,,,,,",
  "em_res_power_max,Double,Char.SpectralAxis.Resolution.ResolPower.HiLimit,spect.resolution,High bound of spectral resolving power,,,double,,,,,",
  ",,,,,,,,,,,,",
  ",TimeAxis,Char.TimeAxis,,Time Axis Characterization,,,,,,,,",
  "t_name,String,Char.TimeAxis.Name,meta.id,Name for time axis,,,char,*,,,,",
  "t_ucd,String,Char.TimeAxis.UCD,meta.ucd,UCD for time,,,char,*,,,,",
  "t_unit,String,Char.TimeAxis.Unit,meta.unit,Unit for time,TIMEUNIT,,char,*,,s,,",
  "t_midpoint,Double,Char.TimeAxis.Coverage.Location.Coord.Time.TimeInstant,VOX:Image_MJDateObs,Midpoint of exposure on MJD scale,DATE-OBS,,double,,d,d,q,",
  "t_extent,Double,Char.TimeAxis.Coverage.Bounds.Extent,time.duration;obs.exposure,Total exposure time,TELAPSE,,double,,,s,,",
  "t_min,Double,Char.TimeAxis.Coverage.Bounds.Limits.StartTime,time.start;obs.exposure,Start time,TSTART,,double,,,d,,",
  "t_max,Double,Char.TimeAxis.Coverage.Bounds.Limits.StopTime,time.stop;obs.exposure,Stop time,TSTOP,,double,,,d,,",
  "t_exptime,Double,Char.TimeAxis.Coverage.Support.Extent,time.duration;obs.exposure,Effective exposure time,EXPOSURE,8;@ID,double,,,s,,",
  "t_binsize,Double,Char.TimeAxis.Sampling.SampleExtent,time.interval,Time bin size,,,double,,,s,,",
  "t_fill_factor,Double,Char.TimeAxis.Sampling.RefVal.FillFactor,stat.filling;time,Time sampling filling factor,DTCOR,,double,,,,,",
  "t_stat_error,Double,Char.TimeAxis.Accuracy.StatError.Refval.value,stat.error;time,Time coord statistical error,TIME_ERR,,double,,,s,,",
  "t_sys_error,Double,Char.TimeAxis.Accuracy.SysError.Refval.value,stat.error.sys;time,Time coord systematic error,TME_SYE,,double,,,s,,",
  "t_calib_status,String,Char.TimeAxis.CalibrationStatus,meta.code.qual,Type of coord calibration,TIME_CAL,,char,*,,,,",
  "t_resolution,Double,Char.TimeAxis.Resolution.RefVal.value,time.resolution,Time resolution FWHM,TIME_RES,,double,,,s,,",
  ",,,,,,,,,,,,",
  ",ObservableAxis,Char.ObservableAxis,,Observable Axis Characterization,,,,,,,,",
  "o_name,String,Char.ObservableAxis.Name,meta.id,Name for observable axis,,,char,*,,,,",
  "o_ucd,String,Char.ObservableAxis.UCD,meta.ucd,UCD for observable,,,char,*,,,,",
  "o_unit,String,Char.ObservableAxis.Unit,meta.unit,Unit for observable,,,char,*,,,,",
  "o_stat_error,Double,Char.ObservableAxis.Accuracy.StatError.Refval.value,stat.error;${o_ucd),Observable statistical error,STAT_ERR,,double,,,,,",
  "o_sys_error,Double,Char.ObservableAxis.Accuracy.SysError.Refval.value,stat.error.sys;$(o_ucd),Observable systematic error,SYS_ERR,,double,,,,,",
  "o_calib_status,String,Char.ObservableAxis.CalibrationStatus,meta.code.qual,Level of calibration,FLUX_CAL,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",PolAxis,Char.PolAxis,,Polarization Axis Characterization,,,,,,,,",
  "pol_axis_name,String,Char.PolAxis.Name,meta.id,Name for polarization axis,,,char,*,,,,",
  "pol_ucd,String,Char.PolAxis.UCD,meta.ucd,UCD for polarization type,,,char,*,,,,",
  "pol_states,String,Char.PolAxis.StateList,meta.code;phys.polarization;meta.fits   ,List of polarization states present,,,char,*,,,,",
  ",,,,,,,,,,,,",
  ",,,,,,,,,,,,",
  "#           Field ID,Type,UTYPE,UCD,Description,FITS,CSV,DataType,ArraySize,Unit,SiapUnit,Hint,",
  "## DATA Model,,,,,,,,,,,,",
  ",,Data,,Data Element,,,,,,,,",
  "data_id,String,Data.ID,meta.id,ID string of data element,,,char,*,,,q,",
  "data_naxes,Long,Data.Naxes,pos.wcs.naxes,Number of axes (dimensionality),NAXIS,,long,,,,m,",
  "data_naxis,Long[],Data.Naxis,pos.wcs.naxis,Length of each axis,NAXIS,,long,*,,,m,",
  "data_pixtype,String,Data.Pixtype,meta.code;instr.pixel,Pixel datatype,BITPIX,,char,*,,,q,",
  "data_encoding,String,Data.Encoding,meta.note,Type of encoding used for array data,,,char,*,,,,",
  "data_length,Long,Data.Length,meta.number,Array length in pixels (actual count if sparse),,,long,,,,q,",
  "data_size,Long,Data.Size,meta.number,Array length in bytes,,,long,,,,q,",
  "data_values,Void*,Data.Values,,Array data (not included in all instances),,,byte,*,,,,",
  ",,,,,,,,,,,,",
  ",Mapping,Data.Mapping,,Mapping (WCS) Metadata,,,,,,,,",
  "map_naxes,Long,Data.Mapping.WCSNaxes,meta.number,Number of WCS axes,WCSAXES,,long,,,,m,",
  "map_name,String,Data.Mapping.WCSName,pos.wcs;meta.id,Name for the overall WCS,WCSNAME,,char,*,,,,",
  "map_refpixel,Double[],Data.Mapping.RefPixel,pos.wcs.crpix,Reference pixel,CRPIX,,double,*,,,m,",
  "map_refvalue,Double[],Data.Mapping.RefValue,pos.wcs.crval,WCS value at reference pixel,CRVAL,,double,*,,,m,",
  "map_cdmatrix,Double[],Data.Mapping.CDMatrix,pos.wcs.cdmatrix,Coord definition matrix,CD,,double,*,,,q,",
  "map_pcmatrix,Double[],Data.Mapping.PCMatrix,,Coord definition matrix,PC,,double,*,,,q,",
  "map_cdelt,Double[],Data.Mapping.Cdelt,pos.wcs.scale,World coord increment per pixel,CDELT,,char,*,,,q,",
  "map_axismap,String[],Data.Mapping.AxisMap,,Image-to-WCS axis mapping,,,char,*,,,m,",
  ",Axis,Data.Mapping.Axis,,,,,,,,,,",
  "axis_ctype,String,Data.Mapping.Axis.CoordType,pos.wcs.ctype,Coordinate type,CTYPE,,char,*,,,m,",
  "axis_unit,String,Data.Mapping.Axis.Unit,meta.unit,Coordinate unit,CUNIT,,char,*,,,m,",
  "axis_name,String,Data.Mapping.Axis.Name,meta.id,Axis name,CNAME,,char,*,,,,",
  "axis_cval,Double[],Data.Mapping.Axis.CoordValue,,Table of explicit coordinate values,,,double,*,,,,",
  "axis_cidx,Double[],Data.Mapping.Axis.CoordIndex,,Index into CoordValue,,,double,*,,,,",
  ",SpatialAxis,Data.Mapping.SpatialAxis,,,,,,,,,,",
  "xs_ctype,String,Data.Mapping.SpatialAxis.CoordType,pos.wcs.ctype,Coordinate type for final world coordinates,CTYPE,,char,*,,,m,",
  "xs_algo,String                  ,Data.Mapping.SpatialAxis.Algorithm,,Algorithm used for nonlinear term,CTYPE,,char,*,,,m,",
  "xs_frame,String,Data.Mapping.SpatialAxis.CoordFrame,pos.frame,Coordinate system or frame for final coords,RADECSYS,,char,*,,,m,",
  "xs_equinox,Double                  ,Data.Mapping.SpatialAxis.Equinox,time.equinox,Coordinate equinox if needed for frame,EQUINOX,,double,,,,,",
  "xs_unit,String                  ,Data.Mapping.SpatialAxis.Unit,meta.unit,Unit for coordinate value,CUNIT,,char,*,,,m,",
  "xs_name,String[],Data.Mapping.SpatialAxis.Name,meta.id,Spatial axis names,CNAME,,char,*,,,,",
  "xs_pv,Double[],Data.Mapping.SpatialAxis.PV,,Optional parameters for transform,PV,,double,*,,,,",
  "xs_ps,String[],Data.Mapping.SpatialAxis.PS,,Optional parameters for transform,PS,,char,*,,,,",
  "xs_lonpole,Double,Data.Mapping.SpatialAxis.LonPole,,Native longitude of the celestial pole,LONPOLE,,double,,,,q,",
  "xs_latpole,Double,Data.Mapping.SpatialAxis.LatPole,,Native latitude of the celestial pole,LATPOLE,,double,,,,q,",
  "xs_cv,Double[],Data.Mapping.SpatialAxis.CoordValue,,Table of explicit spatial coordinate values,,,double,*,,,,",
  "xs_cidx1,Double[],Data.Mapping.SpatialAxis.CoordIndex1,,Index for first coordinate,,,double,*,,,,",
  "xs_cidx2,Double[],Data.Mapping.SpatialAxis.CoordIndex2,,Index for second coordinate,,,double,*,,,,",
  ",SpectralAxis,Data.Mapping.SpectralAxis,,,,,,,,,,",
  "xe_ctype,String,Data.Mapping.SpectralAxis.CoordType,pos.wcs.ctype,Coordinate type as in FITS,CTYPE,,char,*,,,m,",
  "xe_algo,String                  ,Data.Mapping.SpectrallAxis.Algorithm,,Algorithm type as in FITS,CTYPE,,char,*,,,m,",
  "xe_restfreq,Double,Data.Mapping.SpectralAxis.RestFreq,,Rest frequency of spectral line,RESTFRQ,,double,,,,,",
  "xe_restwave,Double                  ,Data.Mapping.SpectralAxis.RestWave,,Rest wavelength of spectral line,RESTWAV,,double,,,,,",
  "xe_unit,String                  ,Data.Mapping.SpectralAxis.Unit,meta.unit,Unit for coordinate value,CUNIT,,char,*,,,m,",
  "xe_name,String                  ,Data.Mapping.SpectralAxis.Name,meta.id,Axis name,CNAME,,char,*,,,,",
  "xe_pv,Double[],Data.Mapping.SpectralAxis.PV,,Optional parameters for transform,PV,,double,*,,,,",
  "xe_ps,String[],Data.Mapping.SpectralAxis.PS,,Optional parameters for transform,PS,,char,*,,,,",
  "xe_cval,Double[],Data.Mapping.SpectralAxis.CoordValue,,Table of explicit spectral coordinate values,,,double,*,,,,",
  "xe_cidx,Double[],Data.Mapping.SpectralAxis.CoordIndex,,Index into CoordValue,,,double,*,,,,",
  ",TimeAxis,Data.Mapping.TimeAxis,,,,,,,,,,",
  "xt_ctype,String                  ,Data.Mapping.TimeAxis.CoordType,pos.wcs.ctype,Time scale,CTYPE TIMESYS,,char,*,,,m,",
  "xt_refpos,String,Data.Mapping.TimeAxis.RefPosition,,Time reference position,TREFPOS,,char,*,,,q,",
  "xt_refdir,String,Data.Mapping.TimeAxis.RefDirection,,Time reference direction,TREFDIR,,char,*,,,q,",
  "xt_mjdref,Double,Data.Mapping.TimeAxis.MJDRef,,MJD time zero (for times expressed as offsets),MJDREF,,double,,,,,",
  "xt_unit,String                  ,Data.Mapping.TimeAxis.Unit,meta.unit,Time unit,CUNIT,,char,*,,,m,",
  "xt_name,String                  ,Data.Mapping.TimeAxis.Name,meta.id,Time axis name,CNAME,,char,*,,,,",
  "xt_cv,Double[]                  ,Data.Mapping.TimeAxis.CoordValue,,Table of explicit time coordinate values,,,double,*,,,,",
  "xt_cidx,Double[],Data.Mapping.TimeAxis.CoordIndex,,Index into CoordValue,,,double,*,,,,",
  ",PolAxis,Data.Mapping.PolAxis,,,,,,,,,,",
  "xp_ctype,String,Data.Mapping.PolAxis.CoordType,pos.wcs.ctype,Coordinate type as in FITS,CTYPE,,char,*,,,q,",
  "xp_name,String                  ,Data.Mapping.PolAxis.Name,meta.id,Polarization axis name,CNAME,,char,*,,,,",
  "xp_state,String[],Data.Mapping.PolAxis.CoordValue,,Polarization state at pixel index,,,char,*,,,m,",
  ",,,,,,,,,,,,",
  ",ObsData,Data.ObsData,,Observational Data Reference,,,,,,,,",
  "obsdata_type,String,Data.ObsData.DataProductType,,Primary data product type (as in ObsCoreDM),,,char,*,,,m,",
  "obsdata_subtype,String,Data.ObsData.DataProductSubtype,,Data product specific type,,,char,*,,,m,",
  "obsdata_calib_level,Long,Data.ObsData.CalibLevel,,Calibration level,,,long,,,,q,",
  "obsdata_url,URL,Data.ObsData.Reference,meta.ref.url,URL used to access the dataset ,,,char,*,,,m,",
  "obsdata_format,String,Data.ObsData.Format,,Content format of the dataset,,,char,*,,,q,",
  "obsdata_estsize,Long,Data.ObsData.Size,,Estimated dataset size,,,long,,kbyte,,q,",
  };
}
