package com.konloch;

import com.konloch.audio.SoundBank;

import java.util.Random;

/**
 * @author Konloch
 * @since 6/24/2024
 */
public class LWJGLAudioExample
{
	public static void main(String[] args) throws InterruptedException
	{
		SoundBank.initialize();
		SoundBank.setListenerLocation(0, 0, 0);
		Random random = new Random();
		
		float x = 0;
		while(true)
		{
			//TODO to play 2d sounds without any 3d impact, just do
			//  SoundBank.getSound("NAME").play();
			
			//cat meowing 12 steps away
			if(random.nextInt(12) == 2)
				SoundBank.getSound("MEOW").play(12, 0, 0);
			
			//footsteps of someone walking away slowly
			SoundBank.getSound("FOOTSTEPS").play(x, 0, 0);
			
			x += 0.5;
			
			Thread.sleep(1000);
		}
	}
}
