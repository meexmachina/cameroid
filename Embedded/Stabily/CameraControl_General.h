#ifndef _CAMERA_CONTROL_GENERAL_H_
#define _CAMERA_CONTROL_GENERAL_H_

/* Includes: */
#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <stdio.h>

#include <LUFA/Version.h>
#include <LUFA/Drivers/Misc/TerminalCodes.h>
#include <LUFA/Drivers/Peripheral/Serial.h>
#include <LUFA/Drivers/Board/LEDs.h>
#include <LUFA/Drivers/USB/USB.h>

#include "CameraControl_PTPDefinitions.h"


/* Macros: */
#define COMMAND_DATA_TIMEOUT_MS        	10000
#define CAMERA_CONTROL_NOT_CONNECTED	(((USB_HostState != HOST_STATE_Configured) || !(SIInterfaceInfo->State.IsActive)))
#define CHECK_CAMERA_CONNECTION			{if (CAMERA_CONTROL_NOT_CONNECTED) { printf_P(PSTR("Device not connected.\r\n")); return 0; }}

#define RET_CODE_DEV_INFO		1
#define RET_CODE_STORAGE_INFO	2
#define RET_CODE_PROP_DESC		3

/* External Variables: */
extern volatile	uint8_t	g_iDataIsValid;


/* Functions: */
void CameraControl_PTPErrorDescription ( uint16_t errNum );
void CameraControl_DescribePIPE_RWSTREAMError ( uint8_t uiStatus );
void UnicodeToASCII(uint8_t* UnicodeString, char* Buffer);
uint8_t CameraControl_InitiateTransaction ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
											PIMA_Container_t *PIMABlock );
uint8_t CameraControl_GetResponseAndCheck (	USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
											PIMA_Container_t *PIMABlock );
uint8_t CameraControl_OpenSession(USB_ClassInfo_SI_Host_t* SIInterfaceInfo);
uint8_t CameraControl_CloseSession(USB_ClassInfo_SI_Host_t* SIInterfaceInfo);
uint8_t CameraControl_CameraConnected ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );

#endif //_CAMERA_CONTROL_GENERAL_H_
