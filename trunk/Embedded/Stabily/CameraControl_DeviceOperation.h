#ifndef _CAMERA_CONTROL_DEVICE_OPERATION_H_
#define _CAMERA_CONTROL_DEVICE_OPERATION_H_

#include "CameraControl_General.h"
#include "CameraControl_StorageInfo.h"
#include "TransferProtocolDefinitions.h"
#include "CameraControl_PTPDefinitions.h"

//uint16_t CameraControl_DeviceOperation_GetStorageIDs (PTPParams* params, PTPStorageIDs* storageids);
//uint16_t CameraControl_DeviceOperation_GetStorageInfo (PTPParams* params, uint32_t storageid, PTPStorageInfo* storageinfo);

//uint16_t CameraControl_DeviceOperation_GetObjectHandles (PTPParams* params, uint32_t storage,
//				uint32_t objectformatcode,
//				uint32_t associationOH,
//				PTPObjectHandles* objecthandles);

uint16_t CameraControl_DeviceOperation_Capture ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 PTP_STORETYPE_EN enStorageType,
												 PTP_FMT_EN enFileFormat );

uint16_t CameraControl_DeviceOperation_GetPropertyDesc ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 		 PTP_DEVPROPERTY_EN enPropertyType );

uint16_t CameraControl_DeviceOperation_GetPropertyDescBin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 		 	 PTP_DEVPROPERTY_EN enPropertyType, uint16_t transID );
															   
uint16_t CameraControl_DeviceOperation_GetPropertyValBin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 		 	 PTP_DEVPROPERTY_EN enPropertyType, uint16_t transID );				
uint16_t CameraControl_GetPropertyVal32Bit	( USB_ClassInfo_SI_Host_t* SIInterfaceInfo, PTP_DEVPROPERTY_EN enPropertyType, uint32_t *iVal );																	
uint16_t CameraControl_DeviceOperation_SetPropertyValBin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 		   PTP_DEVPROPERTY_EN enPropertyType, uint32_t val );		
#endif // _CAMERA_CONTROL_DEVICE_OPERATION_H_
