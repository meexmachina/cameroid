#include "CameraControl_General.h"
#include "Stabily.h"
#include "Stabily_Shell.h"
#include "CameraControl_DeviceInfo.h"


/*********************************************************************************************************************
 *  Event handler for the USB_DeviceAttached event. This indicates that a device has been attached to the host, and
 *  starts the library USB task to begin the enumeration and USB management process.
 */
void EVENT_USB_Host_DeviceAttached(void)
{
	TP_Outgoing_Event_ST ev;
	g_bCameraConnected = 1;
	g_iDataIsValid = 0;
	
	ev.header.type = TP_EVENT_CAMERA_CONNECTED;
	ev.header.length = 4;
	ev.arg1 = 0;
	
	TP_PushEvent (&ev);
	
	if (g_bQuiteMode) return;
	puts_P(PSTR("Device Attached.\r\n"));
}

/*********************************************************************************************************************
 *  Event handler for the USB_DeviceUnattached event. This indicates that a device has been removed from the host, and
 *  stops the library USB task management process.
 */
void EVENT_USB_Host_DeviceUnattached(void)
{
	TP_Outgoing_Event_ST ev;
	g_bCameraConnected = 0;
	g_iDataIsValid = 0;
	
	ev.header.type = TP_EVENT_CAMERA_DISCONNECTED;
	ev.header.length = 4;
	ev.arg1 = 0;
	
	TP_PushEvent (&ev);
	
	if (g_bQuiteMode) return;
	puts_P(PSTR("\r\nDevice Unattached.\r\n"));
}

/*********************************************************************************************************************
 *  Event handler for the USB_DeviceEnumerationComplete event. This indicates that a device has been successfully
 *  enumerated by the host and is now ready to be used by the application.
 */
void EVENT_USB_Host_DeviceEnumerationComplete(void)
{
	uint16_t ConfigDescriptorSize;
	uint8_t  ConfigDescriptorData[512];

	if (USB_Host_GetDeviceConfigDescriptor(1, &ConfigDescriptorSize, ConfigDescriptorData,
	                                       sizeof(ConfigDescriptorData)) != HOST_GETCONFIG_Successful)
	{
		if (!g_bQuiteMode) puts_P(PSTR("Error Retrieving Configuration Descriptor.\r\n"));
		return;
	}

	if (SI_Host_ConfigurePipes(&DigitalCamera_SI_Interface,
	                           ConfigDescriptorSize, ConfigDescriptorData) != SI_ENUMERROR_NoError)
	{
		if (!g_bQuiteMode) puts_P(PSTR("Attached Device Not a Valid Still Image Class Device.\r\n"));
		return;
	}

	if (USB_Host_SetDeviceConfiguration(1) != HOST_SENDCONTROL_Successful)
	{
		if (!g_bQuiteMode) puts_P(PSTR("Error Setting Device Configuration.\r\n"));
		return;
	}

	if (!g_bQuiteMode) puts_P(PSTR("Camera Device Enumerated.\r\n"));
}

/*********************************************************************************************************************
 *  Event handler for the USB_HostError event. This indicates that a hardware error occurred while in host mode. */
void EVENT_USB_Host_HostError(const uint8_t ErrorCode)
{
	USB_Disable();

	if (!g_bQuiteMode) printf_P(PSTR(ESC_FG_RED "Host Mode Error\r\n -- VBUS voltage dropped under 4.5V\r\n" ESC_FG_WHITE), ErrorCode);

	for(;;);
}

/*********************************************************************************************************************
 *  Event handler for the USB_DeviceEnumerationFailed event. This indicates that a problem occurred while
 *  enumerating an attached USB device.
 */
void EVENT_USB_Host_DeviceEnumerationFailed(const uint8_t ErrorCode,
                                            const uint8_t SubErrorCode)
{
	if (!g_bQuiteMode) printf_P(PSTR(ESC_FG_RED "Dev Enum Error\r\n"
	                         " -- Error Code %d\r\n"
	                         " -- Sub Error Code %d\r\n"
	                         " -- In State %d\r\n" ESC_FG_WHITE), ErrorCode, SubErrorCode, USB_HostState);

}

