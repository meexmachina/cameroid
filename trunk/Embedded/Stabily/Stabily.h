#ifndef _STILL_IMAGE_HOST_H_
#define _STILL_IMAGE_HOST_H_

/* Includes: */
#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <stdio.h>
#include <stdlib.h>
#include <LUFA/Drivers/Peripheral/Serial.h>
#include "CameraControl_General.h"

/* Macros: */


/* Function Prototypes: */
void Stabily_SetupHardware(void);
void Stabily_Task(void);

void EVENT_USB_Host_HostError(const uint8_t ErrorCode);
void EVENT_USB_Host_DeviceAttached(void);
void EVENT_USB_Host_DeviceUnattached(void);
void EVENT_USB_Host_DeviceEnumerationFailed(const uint8_t ErrorCode,
                                            const uint8_t SubErrorCode);
void EVENT_USB_Host_DeviceEnumerationComplete(void);

#endif

