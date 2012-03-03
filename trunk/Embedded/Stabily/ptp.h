/* ptp.h
 *
 * Copyright (C) 2001-2004 Mariusz Woloszyn <emsi@ipartners.pl>
 *
 *  This file is part of libptp2.
 *
 *  libptp2 is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  libptp2 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with libptp2; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#ifndef __PTP_H__
#define __PTP_H__

#include <stdarg.h>

/* PTP datalayer byteorder */

#define PTP_DL_BE			0xF0
#define	PTP_DL_LE			0x0F

/* PTP request/response/event general PTP container (transport independent) */

struct _PTPEventData {
	uint16_t eventCode;
	uint16_t eventData1;
	uint16_t eventData2;
};

typedef struct _PTPEventData PTPEventData;

struct _PTPContainer {
	uint16_t Code;
	uint32_t SessionID;
	uint32_t Transaction_ID;
	/* params  may be of any type of size less or equal to uint32_t */
	uint32_t Param1;
	uint32_t Param2;
	uint32_t Param3;
	/* events can only have three parameters */
	uint32_t Param4;
	uint32_t Param5;
	/* the number of meaningfull parameters */
	uint8_t	 Nparam;
};
typedef struct _PTPContainer PTPContainer;

/* PTP USB Bulk-Pipe container */
/* USB bulk max max packet length for high speed endpoints */
#define PTP_USB_BULK_HS_MAX_PACKET_LEN	128
#define PTP_USB_BULK_HDR_LEN		(2*sizeof(uint32_t)+2*sizeof(uint16_t))
#define PTP_USB_BULK_PAYLOAD_LEN	(PTP_USB_BULK_HS_MAX_PACKET_LEN-PTP_USB_BULK_HDR_LEN)
#define PTP_USB_BULK_REQ_LEN	(PTP_USB_BULK_HDR_LEN+5*sizeof(uint32_t))

struct _PTPUSBBulkContainer {
	uint32_t length;
	uint16_t type;
	uint16_t code;
	uint32_t trans_id;
	union {
		struct {
			uint32_t param1;
			uint32_t param2;
			uint32_t param3;
			uint32_t param4;
			uint32_t param5;
		} params;
		unsigned char data[PTP_USB_BULK_PAYLOAD_LEN];
	} payload;
};
typedef struct _PTPUSBBulkContainer PTPUSBBulkContainer;

#define PTP_USB_INT_PACKET_LEN	8

/* PTP USB Asynchronous Event Interrupt Data Format */
struct _PTPUSBEventContainer {
	uint32_t length;
	uint16_t type;
	uint16_t code;
	uint32_t trans_id;
	uint32_t param1;
	uint32_t param2;
	uint32_t param3;
};
typedef struct _PTPUSBEventContainer PTPUSBEventContainer;


/* USB container types */
#define PTP_USB_CONTAINER_UNDEFINED		0x0000
#define PTP_USB_CONTAINER_COMMAND		0x0001
#define PTP_USB_CONTAINER_DATA			0x0002
#define PTP_USB_CONTAINER_RESPONSE		0x0003
#define PTP_USB_CONTAINER_EVENT			0x0004


/* PTP device info structure (returned by GetDevInfo) */

struct _PTPDeviceInfo {
	uint16_t StaqndardVersion;
	uint32_t VendorExtensionID;
	uint16_t VendorExtensionVersion;
	char	*VendorExtensionDesc;
	uint16_t FunctionalMode;
	uint32_t OperationsSupported_len;
	uint16_t *OperationsSupported;
	uint32_t EventsSupported_len;
	uint16_t *EventsSupported;
	uint32_t DevicePropertiesSupported_len;
	uint16_t *DevicePropertiesSupported;
	uint32_t CaptureFormats_len;
	uint16_t *CaptureFormats;
	uint32_t ImageFormats_len;
	uint16_t *ImageFormats;
	char	*Manufacturer;
	char	*Model;
	char	*DeviceVersion;
	char	*SerialNumber;
};
typedef struct _PTPDeviceInfo PTPDeviceInfo;

/* PTP storageIDs structute (returned by GetStorageIDs) */

struct _PTPStorageIDs {
	uint32_t n;
	uint32_t *Storage;
};
typedef struct _PTPStorageIDs PTPStorageIDs;

/* PTP StorageInfo structure (returned by GetStorageInfo) */
struct _PTPStorageInfo {
	uint16_t StorageType;
	uint16_t FilesystemType;
	uint16_t AccessCapability;
	uint64_t MaxCapability;
	uint64_t FreeSpaceInBytes;
	uint32_t FreeSpaceInImages;
	char 	*StorageDescription;
	char	*VolumeLabel;
};
typedef struct _PTPStorageInfo PTPStorageInfo;

/* PTP objecthandles structure (returned by GetObjectHandles) */

struct _PTPObjectHandles {
	uint32_t n;
	uint32_t *Handler;
};
typedef struct _PTPObjectHandles PTPObjectHandles;

#define PTP_HANDLER_SPECIAL	0xffffffff
#define PTP_HANDLER_ROOT	0x00000000


/* PTP objectinfo structure (returned by GetObjectInfo) */

struct _PTPObjectInfo {
	uint32_t StorageID;
	uint16_t ObjectFormat;
	uint16_t ProtectionStatus;
	uint32_t ObjectCompressedSize;
	uint16_t ThumbFormat;
	uint32_t ThumbCompressedSize;
	uint32_t ThumbPixWidth;
	uint32_t ThumbPixHeight;
	uint32_t ImagePixWidth;
	uint32_t ImagePixHeight;
	uint32_t ImageBitDepth;
	uint32_t ParentObject;
	uint16_t AssociationType;
	uint32_t AssociationDesc;
	uint32_t SequenceNumber;
	char 	*Filename;
	uint32_t CaptureDate;
	uint32_t ModificationDate;
	char	*Keywords;
};
typedef struct _PTPObjectInfo PTPObjectInfo;

/* max ptp string length INCLUDING terminating null character */
#define PTP_MAXSTRLEN				255

/* PTP Object Format Codes */


/* Property Describing Dataset, Range Form */

struct _PTPPropDescRangeForm {
	void *		MinimumValue;
	void *		MaximumValue;
	void *		StepSize;
};
typedef struct _PTPPropDescRangeForm PTPPropDescRangeForm;

/* Property Describing Dataset, Enum Form */

struct _PTPPropDescEnumForm {
	uint16_t	NumberOfValues;
	void **		SupportedValue;
};
typedef struct _PTPPropDescEnumForm PTPPropDescEnumForm;

/* Device Property Describing Dataset (DevicePropDesc) */

struct _PTPDevicePropDesc {
	uint16_t	DevicePropertyCode;
	uint16_t	DataType;
	uint8_t		GetSet;
	void *		FactoryDefaultValue;
	void *		CurrentValue;
	uint8_t		FormFlag;
	union	{
		PTPPropDescEnumForm	Enum;
		PTPPropDescRangeForm	Range;
	} FORM;
};
typedef struct _PTPDevicePropDesc PTPDevicePropDesc;

/* Canon filesystem's folder entry Dataset */

#define PTP_CANON_FilenameBufferLen	13
#define PTP_CANON_FolderEntryLen	sizeof(PTPCANONFolderEntry)

struct _PTPCANONFolderEntry {
	uint32_t	ObjectHandle;
	uint16_t	ObjectFormatCode;
	uint8_t		Flags;
	uint32_t	ObjectSize;
	uint32_t	Time;
    char     Filename[PTP_CANON_FilenameBufferLen];
};
typedef struct _PTPCANONFolderEntry PTPCANONFolderEntry;

/* Glue stuff starts here */

typedef struct _PTPParams PTPParams;

/* raw write functions */
typedef short (* PTPIOReadFunc)	(unsigned char *bytes, unsigned int size,
				 void *data);
typedef short (* PTPIOWriteFunc)(unsigned char *bytes, unsigned int size,
				 void *data);
/*
 * This functions take PTP oriented arguments and send them over an
 * appropriate data layer doing byteorder conversion accordingly.
 */
typedef uint16_t (* PTPIOSendReq)	(PTPParams* params, PTPContainer* req);
typedef uint16_t (* PTPIOSendData)	(PTPParams* params, PTPContainer* ptp,
					unsigned char *data, unsigned int size);
typedef uint16_t (* PTPIOGetResp)	(PTPParams* params, PTPContainer* resp);
typedef uint16_t (* PTPIOGetData)	(PTPParams* params, PTPContainer* ptp,
					unsigned char **data);
/* debug functions */
typedef void (* PTPErrorFunc) (void *data, const char *format, va_list args);
typedef void (* PTPDebugFunc) (void *data, const char *format, va_list args);

struct _PTPParams {
	/* data layer byteorder */
	uint8_t	byteorder;

	/* Data layer IO functions */
	PTPIOReadFunc	read_func;
	PTPIOWriteFunc	write_func;
	PTPIOReadFunc	check_int_func;
	PTPIOReadFunc	check_int_fast_func;

	/* Custom IO functions */
	PTPIOSendReq	sendreq_func;
	PTPIOSendData	senddata_func;
	PTPIOGetResp	getresp_func;
	PTPIOGetData	getdata_func;
	PTPIOGetResp	event_check;
	PTPIOGetResp	event_wait;

	/* Custom error and debug function */
	PTPErrorFunc error_func;
	PTPDebugFunc debug_func;

	/* Data passed to above functions */
	void *data;

	/* ptp transaction ID */
	uint32_t transaction_id;
	/* ptp session ID */
	uint32_t session_id;

	/* internal structures used by ptp driver */
	PTPObjectHandles handles;
	PTPObjectInfo * objectinfo;
	PTPDeviceInfo deviceinfo;
};

/* last, but not least - ptp functions */
uint16_t ptp_usb_sendreq	(PTPParams* params, PTPContainer* req);
uint16_t ptp_usb_senddata	(PTPParams* params, PTPContainer* ptp,
				unsigned char *data, unsigned int size);
uint16_t ptp_usb_getresp	(PTPParams* params, PTPContainer* resp);
uint16_t ptp_usb_getdata	(PTPParams* params, PTPContainer* ptp, 
				unsigned char **data);
uint16_t ptp_usb_event_check	(PTPParams* params, PTPContainer* event);
uint16_t ptp_usb_event_wait		(PTPParams* params, PTPContainer* event);

uint16_t ptp_getdeviceinfo	(PTPParams* params, PTPDeviceInfo* deviceinfo);

uint16_t ptp_opensession	(PTPParams *params, uint32_t session);
uint16_t ptp_closesession	(PTPParams *params);

uint16_t ptp_getstorageids	(PTPParams* params, PTPStorageIDs* storageids);
uint16_t ptp_getstorageinfo 	(PTPParams* params, uint32_t storageid,
				PTPStorageInfo* storageinfo);

uint16_t ptp_getobjecthandles 	(PTPParams* params, uint32_t storage,
				uint32_t objectformatcode,
				uint32_t associationOH,
				PTPObjectHandles* objecthandles);

uint16_t ptp_getobjectinfo	(PTPParams *params, uint32_t handle,
				PTPObjectInfo* objectinfo);

uint16_t ptp_getobject		(PTPParams *params, uint32_t handle,
				char** object);
uint16_t ptp_getthumb		(PTPParams *params, uint32_t handle,
				char** object);

uint16_t ptp_deleteobject	(PTPParams* params, uint32_t handle,
				uint32_t ofc);

uint16_t ptp_sendobjectinfo	(PTPParams* params, uint32_t* store,
				uint32_t* parenthandle, uint32_t* handle,
				PTPObjectInfo* objectinfo);
uint16_t ptp_sendobject		(PTPParams* params, char* object,
				uint32_t size);

uint16_t ptp_initiatecapture	(PTPParams* params, uint32_t storageid,
				uint32_t ofc);

uint16_t ptp_getdevicepropdesc	(PTPParams* params, uint16_t propcode,
				PTPDevicePropDesc *devicepropertydesc);
uint16_t ptp_getdevicepropvalue	(PTPParams* params, uint16_t propcode,
				void** value, uint16_t datatype);
uint16_t ptp_setdevicepropvalue (PTPParams* params, uint16_t propcode,
                        	void* value, uint16_t datatype);


uint16_t ptp_ek_sendfileobjectinfo (PTPParams* params, uint32_t* store,
				uint32_t* parenthandle, uint32_t* handle,
				PTPObjectInfo* objectinfo);
uint16_t ptp_ek_sendfileobject	(PTPParams* params, char* object,
				uint32_t size);
				
/* Canon PTP extensions */

uint16_t ptp_canon_getobjectsize (PTPParams* params, uint32_t handle,
				uint32_t p2, uint32_t* size, uint32_t* rp2);

uint16_t ptp_canon_startshootingmode (PTPParams* params);
uint16_t ptp_canon_endshootingmode (PTPParams* params);

uint16_t ptp_canon_viewfinderon (PTPParams* params);
uint16_t ptp_canon_viewfinderoff (PTPParams* params);

uint16_t ptp_canon_reflectchanges (PTPParams* params, uint32_t p1);
uint16_t ptp_canon_checkevent (PTPParams* params, 
				PTPUSBEventContainer* event, int* isevent);
uint16_t ptp_canon_focuslock (PTPParams* params);
uint16_t ptp_canon_focusunlock (PTPParams* params);
uint16_t ptp_canon_initiatecaptureinmemory (PTPParams* params);
uint16_t ptp_canon_getpartialobject (PTPParams* params, uint32_t handle, 
				uint32_t offset, uint32_t size,
				uint32_t pos, char** block, 
				uint32_t* readnum);
uint16_t ptp_canon_getviewfinderimage (PTPParams* params, char** image,
				uint32_t* size);
uint16_t ptp_canon_getchanges (PTPParams* params, uint16_t** props,
				uint32_t* propnum); 
uint16_t ptp_canon_getfolderentries (PTPParams* params, uint32_t store,
				uint32_t p2, uint32_t parenthandle,
				uint32_t handle, 
				PTPCANONFolderEntry** entries,
				uint32_t* entnum);

/* Nikon extensions */
uint16_t ptp_nikon_setcontrolmode (PTPParams* params, uint32_t mode);
uint16_t ptp_nikon_directcapture (PTPParams* params, uint32_t unknown);
uint16_t ptp_nikon_checkevent (PTPParams* params,
				PTPUSBEventContainer** event, uint16_t* evnum);
uint16_t ptp_nikon_keepalive (PTPParams* params);


/* Non PTP protocol functions */
int ptp_operation_issupported	(PTPParams* params, uint16_t operation);
int ptp_property_issupported	(PTPParams* params, uint16_t property);

void ptp_free_devicepropdesc	(PTPDevicePropDesc* dpd);
void ptp_perror			(PTPParams* params, uint16_t error);
const char*
ptp_get_datatype_name		(PTPParams* params, uint16_t dt);
const char*
ptp_get_operation_name		(PTPParams* params, uint16_t oc);
const char*
ptp_prop_getname		(PTPParams* params, uint16_t dpc);

/* Properties handling functions */
const char* ptp_prop_getdesc	(PTPParams* params, PTPDevicePropDesc *dpd,
					void *val);
const char* ptp_prop_getdescbystring
				(PTPParams* params,PTPDevicePropDesc *dpd,
				const char *strval);

const char * ptp_prop_tostr	(PTPParams* params, PTPDevicePropDesc *dpd,
					void *val);
uint16_t ptp_prop_getcodebyname	(PTPParams* params, char* propname);
const char* ptp_prop_getvalbyname
				(PTPParams* params, char* name, uint16_t dpc);


#endif /* __PTP_H__ */
