#ifndef _CAMERA_CONTROL_DEVICE_EVENTS_H_
#define _CAMERA_CONTROL_DEVICE_EVENTS_H_

#include "CameraControl_General.h"
#include "Stabily.h"

/*------------------------------------------------------------------------------
 * DevEventHandler_t - event handler structure
 */
typedef struct
{
    void (*func_event_hander)(uint32_t param);
	uint8_t last_error;
} DevEventHandler_t;

extern DevEventHandler_t event_hander_tbl[];

/*------------------------------------------------------------------------------
 * CameraControl_DeviceEvents_PollEvents
 */
uint8_t CameraControl_DeviceEvents_PollEvents( USB_ClassInfo_SI_Host_t* SIInterfaceInfo );




#endif	//_CAMERA_CONTROL_DEVICE_EVENTS_H_
