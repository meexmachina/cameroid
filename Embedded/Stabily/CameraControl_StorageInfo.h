#ifndef _CAMERA_CONTROL_STORAGE_INFO_H_
#define _CAMERA_CONTROL_STORAGE_INFO_H_

#include "CameraControl_General.h"
#include "TransferProtocolDefinitions.h"
#include "CameraControl_PTPDefinitions.h"

/* Macros: */
#define			MAX_NUM_STORAGES			4
#define 		STORAGE_MAX_STRING_SIZE		16

/* Structs: */
typedef struct {
	uint16_t	iStorageType;
	uint16_t	iFSType;
	uint16_t	iAccessCap;
	uint64_t	iMaxCapacity;
	uint64_t	iFreeSpaceBytes;
	uint32_t	iFreeSpaceImages;
	char		sDescription [STORAGE_MAX_STRING_SIZE];
	char		sVolumeLabel [STORAGE_MAX_STRING_SIZE];
} CameraControl_StorageInfo_ST;

/* Externals: */
extern volatile uint32_t						g_aiStorageIDs[MAX_NUM_STORAGES];
extern volatile CameraControl_StorageInfo_ST	g_astStorageInfo[MAX_NUM_STORAGES];
extern volatile uint8_t							g_iNumOfStorages;

/* Functions: */
uint8_t CameraControl_GetStorageID 			( PTP_STORETYPE_EN enStorageType, uint32_t* iStorageID );
uint8_t CameraControl_GetStorageIDs 		( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );
uint8_t CameraControl_GetStorageInfo 		( USB_ClassInfo_SI_Host_t* SIInterfaceInfo, 
									   		  uint8_t iStorageIndex );
uint8_t CameraControl_StorageInfo_Printout 	( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );
uint8_t CameraControl_StorageInfo_Bin 		( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,  
											  uint8_t iStorageIndex, uint16_t transID );
uint8_t CameraControl_GetStorageID 			( PTP_STORETYPE_EN enStorageType, uint32_t* iStorageID );

#endif // _CAMERA_CONTROL_STORAGE_INFO_H_
