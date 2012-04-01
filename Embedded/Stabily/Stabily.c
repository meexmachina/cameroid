#include "Stabily.h"

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

volatile uint8_t g_bQuiteMode = 1;
volatile uint8_t g_bCameraConnected = 0;
	
/*********************************************************************************************************************
 *  Main program entry point. This routine configures the hardware required by the application, then
 *  enters a loop to run the application tasks in sequence.
 */
int main(void)
{
	Stabily_SetupHardware(  );
	
	sei();

	for (;;)
	{
		TP_SendEvent (  );
		//Stabily_ShellRX ( );
		TP_GetIncomingCommand (  );		
		
		CameraControl_DeviceEvents_PollEvents(&DigitalCamera_SI_Interface);
		SI_Host_USBTask(&DigitalCamera_SI_Interface);
		USB_USBTask();
		TP_CollectEvents (  );
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

	/* UART Initialization */
	uart_init(UART_BAUD_SELECT(115200,F_CPU));
	
	LEDs_Init();
	USB_Init();
}

