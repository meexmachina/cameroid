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
volatile uint8_t g_iSlowEventCount = 0;
volatile uint32_t g_iEventCurrentCount = 0;	
	
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
		SI_Host_USBTask(&DigitalCamera_SI_Interface);
		USB_USBTask(  );	
		
		TP_SendEvent (  );
		//Stabily_ShellRX ( );
		TP_GetIncomingCommand (  );		
		CameraControl_DeviceEvents_PollEvents(&DigitalCamera_SI_Interface);
		TP_CollectEvents (  );
		Stabily_PropertyValueEvent (  );
	}
}

/*********************************************************************************************************************
 *  Configures the board hardware and chip peripherals for the demo's functionality.
 */
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
	
	g_iEventCurrentCount = 0;
}

/*********************************************************************************************************************
 * Check property value updating timing 
 */
void Stabily_PropertyValueEvent ( void )
{
	// Event timing counters
	g_iEventCurrentCount++;
	if (g_iEventCurrentCount==((uint32_t)(80000)))		// every xxx counts
	{
		TP_SendSyncWord ( );
		g_iEventCurrentCount = 0;
		g_iSlowEventCount++;
		if (g_iSlowEventCount==3)
		{
			g_iSlowEventCount=0;
			// perform slow update which contains also the fast updates
			g_iCurrentPropEventVector = g_iPropEventVector;
			TP_CheckPropertyEvents (  );				
		}
		else
		{
			g_iCurrentPropEventVector = g_iPropEventFastMode;
			// perform only the fast updates
			TP_CheckPropertyEvents (  );
		}
	}
	else
	{
		g_iCurrentPropEventVector = 0;
	}

}