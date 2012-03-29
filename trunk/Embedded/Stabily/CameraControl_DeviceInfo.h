#ifndef _CAMERA_CONTROL_DEVICE_INFO_H_
#define _CAMERA_CONTROL_DEVICE_INFO_H_

#include "CameraControl_General.h"


/* Macros: */
#define 		DEVINFO_MAX_STRING_SIZE		20
#define			OPERATION_SUPPORT_BASE		PTP_OC_Undefined
#define			EVENT_SUPPORT_BASE			PTP_EC_Undefined
#define 		PROPERTY_SUPPORT_BASE		PTP_DPC_Undefined
#define			CAP_FORMAT_SUPPORT_BASE		PTP_OFC_Undefined
#define			IM_FORMAT_SUPPORT_BASE		PTP_OFC_IM_Undefined

#define 		PRINT_CAPS(from,text,base) { 	int i; uint64_t i64TempContain = (from);				\
											 	printf_P(PSTR((text)));									\
												for (i=0; i<64; i++)									\
												{														\
													if ( i64TempContain & 0x1 )							\
														printf_P(PSTR("	(-) 0x%04x\r\n"), (base)+i);	\
													i64TempContain>>=1;									\
												}														\
											}													


/* Structs: */
typedef struct {
	uint16_t			iStandardVersion;
	uint32_t			iVendorExtensionID;
	uint16_t			iVendorExtensionVersion;
	char				sVendorExtensionDescription[DEVINFO_MAX_STRING_SIZE];
	uint16_t			iFunctionMode;
	uint64_t			iOperationsSupported;		// 64bit bitmap - LSB = OPERATION_SUPPORT_BASE; LSB<<1 = PTP_OC_GetDeviceInfo...
	uint64_t			iEventsSupported;			// 64bit bitmap - LSB = EVENT_SUPPORT_BASE
	uint64_t			iPropertiesSupported;		// 64bit bitmap - LSB = PROPERTY_SUPPORT_BASE
	uint64_t			iCaptureFormatsSupported;	// 64bit bitmap - LSB = CAP_FORMAT_SUPPORT_BASE
	uint64_t			iImageFormatsSupported;		// 64bit bitmap - LSB = IM_FORMAT_SUPPORT_BASE
	char				sManufacturer[DEVINFO_MAX_STRING_SIZE];
	char				Model[DEVINFO_MAX_STRING_SIZE];
	char				DeviceVersion[DEVINFO_MAX_STRING_SIZE];
	char				SerialNumber[DEVINFO_MAX_STRING_SIZE];
} CameraControl_DeviceInfo_ST;


/* Functions: */
bool CameraControl_DeviceInfo_PrintString(uint8_t **pp, uint16_t *count);
bool CameraControl_DeviceInfo_CopyString(	uint8_t **pp, uint16_t *count, 
											char *dest, uint8_t maxLen);
bool CameraControl_DeviceInfo_CopyArray(	uint8_t **pp, uint16_t *count, uint8_t elSize, 
											uint64_t *dest, uint16_t base);
bool CameraControl_DeviceInfo_PrintArray(uint8_t **pp, uint16_t *count, uint8_t elementSize);
void CameraControl_DeviceInfo_Parse(uint16_t len, uint8_t *pbuf);
uint8_t CameraControl_DeviceInfo_GetInfo ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );
uint8_t CameraControl_DeviceInfo_Printout ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );
uint8_t CameraControl_DeviceInfo_Bin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo, uint16_t transID );

#endif // _CAMERA_CONTROL_DEVICE_INFO_H_
