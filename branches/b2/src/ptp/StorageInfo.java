package ptp;

import java.io.PrintStream;

/**
 * StorageInfo provides information such as the type and capacity of storage media, whether it's removable, and more.
 * 
 */
public class StorageInfo extends Data
{
	int storageType;
	int filesystemType;
	int accessCapability;
	long maxCapacity;

	long freeSpaceInBytes;
	int freeSpaceInImages;
	String storageDescription;
	String volumeLabel;

	StorageInfo(NameFactory f)
	{
		super(f);
	}
	
	public StorageInfo(NameFactory f, byte[] data)
	{
		super(true, data, data.length, f);
		parse();
	}

	void parse()
	{
		super.parse();

		storageType = nextU16();
		filesystemType = nextU16();
		accessCapability = nextU16();
		maxCapacity = /* unsigned */nextS64();
		freeSpaceInBytes = /* unsigned */nextS64();
		freeSpaceInImages = /* unsigned */nextS32();
		storageDescription = nextString();
		volumeLabel = nextString();
	}

	void line(PrintStream out)
	{
		String temp;

		switch (storageType)
		{
		case 0:
			temp = "undefined";
			break;
		case 1:
			temp = "Fixed ROM";
			break;
		case 2:
			temp = "Removable ROM";
			break;
		case 3:
			temp = "Fixed RAM";
			break;
		case 4:
			temp = "Removable RAM";
			break;
		default:
			temp = "Reserved-0x" + Integer.toHexString(storageType);
		}
		out.println("Storage Type: " + temp);
	}

	void dump(PrintStream out)
	{
		String temp;

		super.dump(out);
		out.println("StorageInfo:");
		line(out);

		switch (filesystemType)
		{
		case 0:
			temp = "undefined";
			break;
		case 1:
			temp = "flat";
			break;
		case 2:
			temp = "hierarchical";
			break;
		case 3:
			temp = "dcf";
			break;
		default:
			if ((filesystemType & 0x8000) != 0)
				temp = "Reserved-0x";
			else
				temp = "Vendor-0x";
			temp += Integer.toHexString(filesystemType);
		}
		out.println("Filesystem Type: " + temp);

		// access: rw, ro, or ro "with object deletion"

		// CF card sizes are "marketing megabytes", not real ones
		if (maxCapacity != ~0)
			out.println("Capacity: " + maxCapacity + " bytes (" + ((maxCapacity + 500000) / 1000000) + " MB)");
		if (freeSpaceInBytes != ~0)
			out.println("Free space: " + freeSpaceInBytes + " bytes (" + ((freeSpaceInBytes + 500000) / 1000000) + " MB)");
		if (freeSpaceInImages != ~0)
			out.println("Free space in Images: " + freeSpaceInImages);

		if (storageDescription != null)
			out.println("Description: " + storageDescription);
		if (volumeLabel != null)
			out.println("Volume Label: " + volumeLabel);
	}
}