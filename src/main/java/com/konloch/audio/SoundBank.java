package com.konloch.audio;

import com.konloch.util.FastStringUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.konloch.util.FastStringUtils.isInteger;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_EXPONENT_DISTANCE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Konloch
 * @since 6/8/2024
 */
public class SoundBank
{
	private static final HashMap<String, ArrayList<Sound>> soundMap = new HashMap<>();
	private static final HashMap<Integer, Sound> soundIDMap = new HashMap<>();
	private static final Random random = new Random();
	
	public static void initialize()
	{
		long device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == NULL)
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		
		long context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == NULL)
			throw new IllegalStateException("Failed to create an OpenAL context.");
		
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(ALC.createCapabilities(device));
		
		//set distance as clamping mode
		alDistanceModel(AL_EXPONENT_DISTANCE);
		
		//load SoundBank
		load();
	}
	
	private static void load()
	{
		int totalSounds = 0;
		
		try
		{
			for(String line : readSoundBank("SoundBank.txt"))
			{
				String[] parts = FastStringUtils.split(line, "=");
				if (parts.length == 2)
				{
					String key = parts[0];
					String[] soundInfo = FastStringUtils.split(parts[1], ",");
					if (soundInfo.length == 5)
					{
						totalSounds++;
						
						String path = soundInfo[0];
						float volume = Float.parseFloat(soundInfo[1]);
						float rollOff = Float.parseFloat(soundInfo[2]);
						float referenceDistance = Float.parseFloat(soundInfo[3]);
						float maxDistance = Float.parseFloat(soundInfo[4]);
						
						//if the key ends with "_{number}", merge it with existing entries
						String[] keyParts = FastStringUtils.split(key, "_");
						int lengthBeforeNumerical = keyParts.length-2;
						for(int i = 0; i < keyParts.length-1; i++)
							lengthBeforeNumerical += keyParts[i].length();
						
						String mapKey;
						ArrayList<Sound> sounds;
						if (keyParts.length > 1 && isInteger(keyParts[keyParts.length - 1]))
						{
							mapKey = key.substring(0, lengthBeforeNumerical);
							sounds = soundMap.getOrDefault(mapKey, new ArrayList<>());
						}
						else
						{
							mapKey = key;
							sounds = new ArrayList<>();
						}
						
						Sound sound = new Sound(path, volume, rollOff, referenceDistance, maxDistance);
						soundIDMap.put(sound.getSourceIndex(), sound);
						sounds.add(sound);
						soundMap.put(mapKey, sounds);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Loaded " + totalSounds + " sounds into the sound bank, with " +  soundMap.size() + " triggers.");
	}
	
	public static void setListenerLocation(float x, float y, float z)
	{
		alListener3f(AL_POSITION, x, y, z);
		alListener3f(AL_VELOCITY, 0f, 0f, 0f);
		alListenerfv(AL_ORIENTATION, new float[]{0, 0, -1, 0, 1, 0});
	}
	
	public static ArrayList<String> readSoundBank(String resourcePath) throws IOException
	{
		ArrayList<String> lines = new ArrayList<>();
		
		ClassLoader classLoader = SoundBank.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
		
		if (inputStream != null)
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					lines.add(line.trim());
				}
			}
		}
		
		return lines;
	}
	
	public static Sound getSoundByIndex(int index)
	{
		Sound sound = soundIDMap.get(index);
		
		if(sound == null)
			throw new RuntimeException("Non-existent sound for id: " + index);
		
		return sound;
	}
	
	public static Sound getSound(String name)
	{
		ArrayList<Sound> sounds = soundMap.get(name);
		
		if(sounds == null)
			throw new RuntimeException("Non-existent trigger sound for: " + name);
		
		if(sounds.size() == 1)
			return sounds.get(0);
		else
			return sounds.get(random.nextInt(sounds.size()));
	}
}