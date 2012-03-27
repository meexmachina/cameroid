#include "Stabily.h"
#include "CameraControl_DeviceInfo.h"
#include "Stabily_Shell.h"
#include "CameraControl_DeviceEvents.h"
#include "CameraControl_DeviceInfo.h"
#include "CameraControl_DeviceOperation.h"
#include "CameraControl_General.h"
#include "ISRUart.h"
//#include "CameraControl_StorageInfo.h"
#include <stdlib.h>

/** LUFA Still Image Class driver interface configuration and state information. This structure is
 *  passed to all Still Image Class driver functions, so that multiple instances of the same class
 *  within a device can be differentiated from one another.
 */
USB_ClassInfo_SI_Host_t DigitalCamera_SI_Interface =
	{
		.Config =
			{
				.DataINPipeNumber       = 1,
				.DataINPipeDoubleBank   = false,

				.DataOUTPipeNumber      = 2,
				.DataOUTPipeDoubleBank  = false,

				.EventsPipeNumber       = 3,
				.EventsPipeDoubleBank   = false,
			},
	};
	
/*********************************************************************************************************************
 *  Main program entry point. This routine configures the hardware required by the application, then
 *  enters a loop to run the application tasks in sequence.
 */
int main(void)
{
	Stabily_SetupHardware();
	//puts_P(PSTR(ESC_FG_CYAN "Stabily v1.0 running.\r\n" ESC_FG_WHITE));
	sei();

	for (;;)
	{
		Stabily_ShellRX ( );

		//CameraControl_DeviceEvents_PollEvents(&DigitalCamera_SI_Interface);
		SI_Host_USBTask(&DigitalCamera_SI_Interface);
		USB_USBTask();
	}
}

/*********************************************************************************************************************
 *  Configures the board hardware and chip peripherals for the demo's functionality. */
void Stabily_SetupHardware(void)
{
	/* Disable watchdog if enabled by bootloader/fuses */
	MCUSR &= ~(1 << WDRF);
	wdt_disable();

	/* Disable clock division */
	clock_prescale_set(clock_div_1);

	/* Uart Initialization */
	uart_init(UART_BAUD_SELECT(9600,F_CPU));
	
	//Serial_Init(9600, false);
	LEDs_Init();
	USB_Init();

	/* Create a stdio stream for the serial port for stdin and stdout */
	//Serial_CreateStream(NULL);
}


