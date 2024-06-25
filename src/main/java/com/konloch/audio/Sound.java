package com.konloch.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_FALSE;

/**
 * @author Konloch
 * @since 6/3/2024
 */
public class Sound
{
	//local resource
	private final String resourcePath;
	
	//openAL sound index
	private int sourceIndex;
	
	//runtime sound changes
	private float volume;
	private float rollOff;
	private float referenceDistance;
	private float maxDistance;
	
	public Sound(String resourcePath, float volume, float rollOff, float referenceDistance, float maxDistance)
	{
		this.resourcePath = resourcePath;
		this.volume = volume;
		this.rollOff = rollOff;
		this.referenceDistance = referenceDistance;
		this.maxDistance = maxDistance;
		
		try(AudioInputStream ais = loadFromResource(resourcePath))
		{
			AudioFormat audioFormat = ais.getFormat();
			int format = openALFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
			int sampleRate = (int) audioFormat.getSampleRate();
			int frameSize = audioFormat.getFrameSize();
			int expectedBytes = (int) (ais.getFrameLength() * frameSize);
			
			//copy from AIS to local buffer
			byte[] data = new byte[expectedBytes];
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(expectedBytes);
			int readData = ais.read(data, 0, expectedBytes);
			dataBuffer.put(data, 0, readData);
			((Buffer) dataBuffer).flip();
			
			//load local buffer into openAL
			int bufferIndex = alGenBuffers();
			alBufferData(bufferIndex, format, dataBuffer, sampleRate);
			
			//clear local buffer
			((Buffer) dataBuffer).clear();
			
			sourceIndex = alGenSources();
			alSourcei(sourceIndex, AL_BUFFER, bufferIndex);
			
			System.out.println("Inserting["+ sourceIndex +"]: " + resourcePath);
		}
		catch (UnsupportedAudioFileException | IOException e)
		{
			e.printStackTrace();
		}
		
		int error = alGetError();
		if (error != AL_NO_ERROR)
			System.out.println("OPENAL ERROR: " + getALErrorString(error) + " ON ID: " + sourceIndex);
	}
	
	public int getSourceIndex()
	{
		return sourceIndex;
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
	}
	
	public float getVolume()
	{
		return volume;
	}
	
	public void play()
	{
		play(0, 0, 0, false);
	}
	
	public void play(float x, float y, float z)
	{
		play(x, y, z, true);
	}
	
	public void play(float x, float y, float z, boolean audio3D)
	{
		System.out.println("PLAYING: " + resourcePath + " at " + x + ", " + y + ", " + z);
		
		alSourcef(sourceIndex, AL_ROLLOFF_FACTOR, rollOff);
		alSourcef(sourceIndex, AL_REFERENCE_DISTANCE, referenceDistance);
		alSourcef(sourceIndex, AL_MAX_DISTANCE, maxDistance);
		alSourcef(sourceIndex, AL_GAIN, volume);
		alSourcef(sourceIndex, AL_PITCH, 1f);
		
		if(audio3D)
		{
			alSourcei(sourceIndex, AL_SOURCE_RELATIVE, AL_FALSE);
			alSource3f(sourceIndex, AL_POSITION, x, y, z);
		}
		else
		{
			alSourcei(sourceIndex, AL_SOURCE_RELATIVE, AL_TRUE);
			alSource3f(sourceIndex, AL_POSITION, 0, 0, 0);
		}
		
		alSource3f(sourceIndex, AL_VELOCITY, 1f, 1f, 1f);
		alSourcei(sourceIndex, AL_LOOPING, AL_FALSE);
		
		alSourcePlay(sourceIndex);
		
		int error = alGetError();
		if (error != AL_NO_ERROR)
			System.out.println("OPENAL ERROR: " + getALErrorString(error) + " ON ID: " + sourceIndex);
	}
	
	public static String getALErrorString(int errorCode)
	{
		switch (errorCode)
		{
			case AL10.AL_NO_ERROR:
				return "No error";
			case AL10.AL_INVALID_NAME:
				return "Invalid name parameter";
			case AL10.AL_INVALID_ENUM:
				return "Invalid enum parameter value";
			case AL10.AL_INVALID_VALUE:
				return "Invalid parameter value";
			case AL10.AL_INVALID_OPERATION:
				return "Invalid operation";
			case AL10.AL_OUT_OF_MEMORY:
				return "Out of memory";
			default:
				return "Unknown error code: " + errorCode;
		}
	}
	
	private static int openALFormat(int channels, int bitsPerSample)
	{
		if (channels == 1)
		{
			if (bitsPerSample == 8)
				return AL10.AL_FORMAT_MONO8;
			else if (bitsPerSample == 16)
				return AL10.AL_FORMAT_MONO16;
		}
		else if (channels == 2)
		{
			if (bitsPerSample == 8)
				return AL10.AL_FORMAT_STEREO8;
			else if (bitsPerSample == 16)
				return AL10.AL_FORMAT_STEREO16;
		}
		
		throw new RuntimeException("Format is not compatible: " + channels + ":" + bitsPerSample);
	}
	
	private static AudioInputStream loadFromResource(String path) throws UnsupportedAudioFileException, IOException
	{
		InputStream stream = Sound.class.getResourceAsStream("/" + path);
		
		if (stream == null)
			throw new RuntimeException("Resource not found: " + path);
		
		return AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
	}
}