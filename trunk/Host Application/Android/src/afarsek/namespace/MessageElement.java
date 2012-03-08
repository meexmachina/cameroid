package afarsek.namespace;

public class MessageElement
{
	enum MessageTags
	{
		ME_NO_MSG, ME_STATUS, ME_DEVICE_INFO, ME_STORAGE_INFO, ME_PROPERTY_DESC;
	}

	public byte[] mData;
	public MessageTags mTag;

	public MessageElement(byte[] data, MessageTags tag)
	{
		mData = data.clone();
		mTag = tag;
	}
}
