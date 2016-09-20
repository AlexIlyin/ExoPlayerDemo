package com.alexilyin.android.demo.exoplayerdemo;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class MainActivity extends AppCompatActivity {

    SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*

        ExoPlayer - это альтернативный плеер медиаресурсов для Android-проиложений. В отличие от
        стандартного плеера, его проще реализовать, настроить и использовать. Реализован он как
        надстройка над стандартным Android медиа API в виде иерархии интерфейсов и фабрик.

        Прежде всего, нужно подключить библиотеку в build.gradle:
        dependencies {
            compile 'com.google.android.exoplayer:exoplayer:r2.0.0'
        }

        Основной объект с которым придется работать - это объект ExoPlayer. Так как это интерфейс,
        то либо придется писать реализацию саомстоятельно, либо использовать единственную
        встроенную реализацию - SimpleExoPlayer.

        Объект типа ExoPlayer создается с помощью фабрики ExoPlayerFactory:
            ExoPlayerFactory.newSimpleInstance(...);
            ExoPlayerFactory.newInstance(...);

        Основные характеристики будущего плеера задаются во время вызова этих методов. Для этого
        понадобится дополнительно подготовить несколько объектов:
            MediaSource - представляет источник воспроизведения, отвечает за загрузку файла/потока
            LoadControl - занимается управлением буферизацией, правилами подгрузки новых порций данных
            Renderer - занимается непосредственно воспроизведением потока данных, использует медиакодеки
            TrackSelector - связывает соответствующие MediaSource и Renderer

        Для каждого из этих объектов существует стандартная реализация, но при необходимости можно
        сделать свою реализацию любого компонента.

        В простейшем случае создание плеера будет выглядеть так:

        */

        Handler mainHandler = new Handler();    // handler для получения событий от плеера

        player = ExoPlayerFactory.newSimpleInstance(
                this,                                   // контекст
                new DefaultTrackSelector(mainHandler),  // TrackSelector
                new DefaultLoadControl());              // LoadControl

        /*

        Теперь у нас есть плеер, который, однако, не знает что ему проигрывать и где все это показывать.

        Все параметры медиаисточников задаются объектами MediaSource. Так же, как и ExoPlayer, это
        интерфейс. У которого есть несколько реализаций:
            ExtractorMediaSource - Базовая реализация. Загружает данные из uri
            DashMediaSource - загружает данные из источника с адаптивным битрейтом (DASH)
            HlsMediaSource - загружает данные по протоколу HLS (HTTP Live Streaming)
            SsMediaSource - загружает данные по протоколу SmoothStreaming (Microsoft)
            SingleSampleMediaSource - загружает данные из url как простой семпл (например, субтитры)

        Помимо этого есть функциональные реализации:
            ConcatenatingMediaSource - для склеивания нескольких медиаисточников в один последовательный поток
            LoopingMediaSource - для зацикливания одного медиаисточника
            MergingMediaSource - для синхронного проигрвания медиаисточников (субтитры и видео, наложение аудиодорожек)

        Чаще всего мы будем использовать ExtractorMediaSource котооому нужно дать несколько объектов,
        самые интересные из которых DataSource.Factory и ExtractorsFactory. Первый представляет собой
        объект, способный работать с выбранным типом источника. Второй - декодировать поток данных.

        Стандартные реализации интерфейса DataSource.Factory:
            DefaultDataSourceFactory - Универсальная реализация. При необходимости делегирует свои
                                        функции другим фабрикам
            DefaultHttpDataSourceFactory - Позволяет загружать медиаисточники по сети
            FileDataSourceFactory - Позволяет загружать медиаисточники с локальных источников (file/asset/content)
            CacheDataSourceFactory - Позволяет построить кэш медиаисточника (получет на вход другие фабрики)

        Стандартная реализация интерфейса ExtractorsFactory всего одна - DefaultExtractorsFactory,
        и она использует все кодеки встроенные в android из коробки. Если необходимо встроить в плеер
        свой кодек, можно это сделать через свою реализацию ExtractorsFactory.

        DefaultExtractorsFactory умеет работать со следующими кодеками:
            MP4, including M4A (Mp4Extractor)
            fMP4 (FragmentedMp4Extractor)
            Matroska and WebM (MatroskaExtractor)
            Ogg Vorbis/FLAC (OggExtractor
            MP3 (Mp3Extractor)
            AAC (AdtsExtractor)
            MPEG TS (TsExtractor)
            MPEG PS (PsExtractor)
            FLV (FlvExtractor)
            WAV (WavExtractor)
            FLAC (only available if the FLAC extension is built and included)

        В нашем случае мы используем стандартные объекты:

        */


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,               // Context
                "MyExoPlayerDemo"   // user-agent
        );

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


//        String url = "https://r9---sn-n8v7sner.googlevideo.com/videoplayback?signature=63A12BC75076AA0E2F0A3151111397DDD715DBCF.7C737F641C5AAD9D3487B375A53E2870973AAC05&lmt=1469954540889091&itag=22&key=cms1&mime=video%2Fmp4&ipbits=0&requiressl=yes&pl=18&expire=1473955597&sver=3&sparams=dur,ei,expire,id,initcwndbps,ip,ipbits,ipbypass,itag,lmt,mime,mm,mn,ms,mv,pl,ratebypass,requiressl,source,upn&id=o-AKRZiTPogT-fZiLNLdlIS5zeek_8G1O09hkiOR6csqiM&ip=82.142.170.34&ei=rXLaV6m7GovPdYXJj4AJ&dur=205.403&source=youtube&ratebypass=yes&upn=98lZDsD8EhE&title=%D0%93%D0%B8%D0%B4%D1%80%D0%B0%D0%B2%D0%BB%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9+%D0%BF%D1%80%D0%B5%D1%81%D1%81+-+%D0%93%D1%80%D0%B0%D0%BD%D0%B0%D1%82%D0%B0+(%D0%B8%D0%BD%D0%B5%D1%80%D1%82%D0%BD%D0%B0%D1%8F)&req_id=48c1c41ba086a3ee&redirect_counter=2&cms_redirect=yes&ipbypass=yes&mm=30&mn=sn-n8v7sner&ms=nxu&mt=1473934080&mv=m";
        String url = "http://video.roscha-akademii.ru/img/movie/video/O-tom-kak-nauchitsya-pobezhdat-samomu-i-segodnya.mp4";


        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(url),     // uri источника
                dataSourceFactory,  // DataSource.Factory
                extractorsFactory,  // ExtractorsFactory
                mainHandler,        // Handler для получения событий от плеера
                null                // Listener - объект-получатель событий от плеера
        );

        /*

        Для демонстрации и проигрывания есть несколько вариантов. Самый простой - использовать
        библиотечную вьюху SimpleExoPlayerView. В ней уже реализованы базовый функционал интерфейса
        управления проигрванием и Surface на котором демонстрируется видео. Для более сложных случаев
        можно создать свои реализации и передать их плееру.

        В layout добавим:

        <com.google.android.exoplayer2.ui.SimpleExoPlayerView
            android:id="@+id/simpleExoPlayerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

        */

        SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleExoPlayerView);
        simpleExoPlayerView.setPlayer(player);

        /*

        Осталось только скормить источник плееру, вызвав метод prepare()

        */

        player.prepare(videoSource);
        player.setPlayWhenReady(true);

    }


    @Override
    protected void onDestroy() {

        /*
        Не забываем освобождать ресурсы
         */
        player.release();
        super.onDestroy();
    }
}
