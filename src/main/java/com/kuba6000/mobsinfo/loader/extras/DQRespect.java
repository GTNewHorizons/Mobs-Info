package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import dqr.api.Blocks.DQDecorates;
import dqr.api.Blocks.DQMobFigures;
import dqr.api.Blocks.DQMobObjects;
import dqr.api.Items.DQAccessories;
import dqr.api.Items.DQEmblems;
import dqr.api.Items.DQMiscs;
import dqr.api.enums.EnumDqmMobRoot;
import dqr.entity.mobEntity.DqmMobBase;
import dqr.entity.mobEntity.monsterBoss.DqmEntityDesupisaro1;
import dqr.entity.mobEntity.monsterBoss.DqmEntityDesupisaro2;
import dqr.entity.mobEntity.monsterBoss.DqmEntityDesupisaro3;
import dqr.entity.mobEntity.monsterBoss.DqmEntityEsterk;
import dqr.entity.mobEntity.monsterBoss.DqmEntityGod;
import dqr.entity.mobEntity.monsterBoss.DqmEntityMasterdoragon;
import dqr.entity.mobEntity.monsterBoss.DqmEntityRyuuou;
import dqr.entity.mobEntity.monsterBoss.DqmEntityRyuuou2;
import dqr.entity.mobEntity.monsterBoss.DqmEntityZoma;
import dqr.entity.mobEntity.monsterBoss.DqmMobBaseBoss;
import dqr.entity.mobEntity.monsterDay.DqmEntityAyasiikage;
import dqr.entity.mobEntity.monsterDay.DqmEntityBigCrow;
import dqr.entity.mobEntity.monsterDay.DqmEntityBigguhatto;
import dqr.entity.mobEntity.monsterDay.DqmEntityBubsura;
import dqr.entity.mobEntity.monsterDay.DqmEntityBurauni;
import dqr.entity.mobEntity.monsterDay.DqmEntityButisuraimu;
import dqr.entity.mobEntity.monsterDay.DqmEntityDokuroarai;
import dqr.entity.mobEntity.monsterDay.DqmEntityDoronuba;
import dqr.entity.mobEntity.monsterDay.DqmEntityDorozara;
import dqr.entity.mobEntity.monsterDay.DqmEntityDoruido;
import dqr.entity.mobEntity.monsterDay.DqmEntityDragosuraimu;
import dqr.entity.mobEntity.monsterDay.DqmEntityDucksbill;
import dqr.entity.mobEntity.monsterDay.DqmEntityEbiruapple;
import dqr.entity.mobEntity.monsterDay.DqmEntityFaratto;
import dqr.entity.mobEntity.monsterDay.DqmEntityGaikotu;
import dqr.entity.mobEntity.monsterDay.DqmEntityGizumoAZ;
import dqr.entity.mobEntity.monsterDay.DqmEntityGuntaigani;
import dqr.entity.mobEntity.monsterDay.DqmEntityHitokuikibako;
import dqr.entity.mobEntity.monsterDay.DqmEntityHitokuisaberu;
import dqr.entity.mobEntity.monsterDay.DqmEntityHoimisura;
import dqr.entity.mobEntity.monsterDay.DqmEntityIkkakuusagi;
import dqr.entity.mobEntity.monsterDay.DqmEntityItamogu;
import dqr.entity.mobEntity.monsterDay.DqmEntityKimera;
import dqr.entity.mobEntity.monsterDay.DqmEntityKirapan;
import dqr.entity.mobEntity.monsterDay.DqmEntityKirikabuobake;
import dqr.entity.mobEntity.monsterDay.DqmEntityMadohando;
import dqr.entity.mobEntity.monsterDay.DqmEntityMomon;
import dqr.entity.mobEntity.monsterDay.DqmEntityMomonja;
import dqr.entity.mobEntity.monsterDay.DqmEntityObakekinoko;
import dqr.entity.mobEntity.monsterDay.DqmEntityObakeumiusi;
import dqr.entity.mobEntity.monsterDay.DqmEntityOnikozou;
import dqr.entity.mobEntity.monsterDay.DqmEntityOokiduti;
import dqr.entity.mobEntity.monsterDay.DqmEntityOokutibasi;
import dqr.entity.mobEntity.monsterDay.DqmEntityOomedama;
import dqr.entity.mobEntity.monsterDay.DqmEntityOonamekuji;
import dqr.entity.mobEntity.monsterDay.DqmEntityPapetkozou;
import dqr.entity.mobEntity.monsterDay.DqmEntityPurizunyan;
import dqr.entity.mobEntity.monsterDay.DqmEntityRemonsuraimu;
import dqr.entity.mobEntity.monsterDay.DqmEntityRippusu;
import dqr.entity.mobEntity.monsterDay.DqmEntityRiripat;
import dqr.entity.mobEntity.monsterDay.DqmEntitySabotenboru;
import dqr.entity.mobEntity.monsterDay.DqmEntitySibirekurage;
import dqr.entity.mobEntity.monsterDay.DqmEntitySimasimacat;
import dqr.entity.mobEntity.monsterDay.DqmEntitySirudokozou;
import dqr.entity.mobEntity.monsterDay.DqmEntitySukippaa;
import dqr.entity.mobEntity.monsterDay.DqmEntitySunomon;
import dqr.entity.mobEntity.monsterDay.DqmEntitySupini;
import dqr.entity.mobEntity.monsterDay.DqmEntitySura;
import dqr.entity.mobEntity.monsterDay.DqmEntitySuraimubesu;
import dqr.entity.mobEntity.monsterDay.DqmEntitySuraimunaito;
import dqr.entity.mobEntity.monsterDay.DqmEntitySuraimutawa;
import dqr.entity.mobEntity.monsterDay.DqmEntitySuraimutumuri;
import dqr.entity.mobEntity.monsterDay.DqmEntityTogebouzu;
import dqr.entity.mobEntity.monsterDay.DqmEntityTukaima;
import dqr.entity.mobEntity.monsterDay.DqmEntityUzusioking;
import dqr.entity.mobEntity.monsterDay.DqmEntityWaraibukuro;
import dqr.entity.mobEntity.monsterDay.DqmEntityZinmentyou;
import dqr.entity.mobEntity.monsterDay.DqmEntityZukkinya;
import dqr.entity.mobEntity.monsterDay.DqmMobBaseDay;
import dqr.entity.mobEntity.monsterEnd.DqmEntityAkairai;
import dqr.entity.mobEntity.monsterEnd.DqmEntityBassaimasin;
import dqr.entity.mobEntity.monsterEnd.DqmEntityBatorurex;
import dqr.entity.mobEntity.monsterEnd.DqmEntityBiggumoai;
import dqr.entity.mobEntity.monsterEnd.DqmEntityBurizado;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDakuhobitto;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDakunaito;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDarktororu;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDasudragon;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDenga;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDesujakkaru;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDesunyago;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDesusutoka;
import dqr.entity.mobEntity.monsterEnd.DqmEntityDragondarknaito;
import dqr.entity.mobEntity.monsterEnd.DqmEntityGamegonrejendo;
import dqr.entity.mobEntity.monsterEnd.DqmEntityGigantesu;
import dqr.entity.mobEntity.monsterEnd.DqmEntityGodraida;
import dqr.entity.mobEntity.monsterEnd.DqmEntityGorudensuraimu;
import dqr.entity.mobEntity.monsterEnd.DqmEntityJigokunoyoroi;
import dqr.entity.mobEntity.monsterEnd.DqmEntityKagenokisi;
import dqr.entity.mobEntity.monsterEnd.DqmEntityManemane;
import dqr.entity.mobEntity.monsterEnd.DqmEntityPandorabox;
import dqr.entity.mobEntity.monsterEnd.DqmEntityRyuiso;
import dqr.entity.mobEntity.monsterEnd.DqmEntityShadopan2;
import dqr.entity.mobEntity.monsterEnd.DqmEntityTororubonba;
import dqr.entity.mobEntity.monsterEnd.DqmMobBaseEnd;
import dqr.entity.mobEntity.monsterEtc.DqmEntityFurosutogizumo;
import dqr.entity.mobEntity.monsterEtc.DqmEntityGizumo;
import dqr.entity.mobEntity.monsterEtc.DqmEntityHiitogizumo;
import dqr.entity.mobEntity.monsterEtc.DqmEntityHitokuibako;
import dqr.entity.mobEntity.monsterEtc.DqmEntityKingbesu;
import dqr.entity.mobEntity.monsterEtc.DqmEntityMimikkukibako;
import dqr.entity.mobEntity.monsterEtc.DqmEntityNorowaretaturugi;
import dqr.entity.mobEntity.monsterEtc.DqmEntityPandorakibako;
import dqr.entity.mobEntity.monsterEtc.DqmEntitySuraimuking;
import dqr.entity.mobEntity.monsterEtc.DqmEntityTubo;
import dqr.entity.mobEntity.monsterHell.DqmEntityAnkokumajin;
import dqr.entity.mobEntity.monsterHell.DqmEntityAroinpu;
import dqr.entity.mobEntity.monsterHell.DqmEntityBaburuking;
import dqr.entity.mobEntity.monsterHell.DqmEntityBarakku;
import dqr.entity.mobEntity.monsterHell.DqmEntityBariidodog;
import dqr.entity.mobEntity.monsterHell.DqmEntityBehomasuraimu;
import dqr.entity.mobEntity.monsterHell.DqmEntityBiggufeisu;
import dqr.entity.mobEntity.monsterHell.DqmEntityBighanma;
import dqr.entity.mobEntity.monsterHell.DqmEntityBosutororu;
import dqr.entity.mobEntity.monsterHell.DqmEntityBoureikensi;
import dqr.entity.mobEntity.monsterHell.DqmEntityBuraddosodo;
import dqr.entity.mobEntity.monsterHell.DqmEntityBurakkubejita;
import dqr.entity.mobEntity.monsterHell.DqmEntityDarkdoriado;
import dqr.entity.mobEntity.monsterHell.DqmEntityDarkslime;
import dqr.entity.mobEntity.monsterHell.DqmEntityDeddopekka;
import dqr.entity.mobEntity.monsterHell.DqmEntityDgizumo;
import dqr.entity.mobEntity.monsterHell.DqmEntityDollmaster;
import dqr.entity.mobEntity.monsterHell.DqmEntityDoragonsoruja;
import dqr.entity.mobEntity.monsterHell.DqmEntityDqmdragon;
import dqr.entity.mobEntity.monsterHell.DqmEntityDragonnaito;
import dqr.entity.mobEntity.monsterHell.DqmEntityDragonraida;
import dqr.entity.mobEntity.monsterHell.DqmEntityEriminator;
import dqr.entity.mobEntity.monsterHell.DqmEntityFureimu;
import dqr.entity.mobEntity.monsterHell.DqmEntityGamegon;
import dqr.entity.mobEntity.monsterHell.DqmEntityGamegonload;
import dqr.entity.mobEntity.monsterHell.DqmEntityGanirasu;
import dqr.entity.mobEntity.monsterHell.DqmEntityGoldman;
import dqr.entity.mobEntity.monsterHell.DqmEntityGoremu;
import dqr.entity.mobEntity.monsterHell.DqmEntityGorudentotemu;
import dqr.entity.mobEntity.monsterHell.DqmEntityHotatewarabi;
import dqr.entity.mobEntity.monsterHell.DqmEntityJigokunohasami;
import dqr.entity.mobEntity.monsterHell.DqmEntityKedamon;
import dqr.entity.mobEntity.monsterHell.DqmEntityKemunkurusu;
import dqr.entity.mobEntity.monsterHell.DqmEntityKimendousi;
import dqr.entity.mobEntity.monsterHell.DqmEntityKiraama;
import dqr.entity.mobEntity.monsterHell.DqmEntityKirakurabu;
import dqr.entity.mobEntity.monsterHell.DqmEntityKiramasin;
import dqr.entity.mobEntity.monsterHell.DqmEntityKiramasin2;
import dqr.entity.mobEntity.monsterHell.DqmEntityKisudragon;
import dqr.entity.mobEntity.monsterHell.DqmEntityKuinsuraimu;
import dqr.entity.mobEntity.monsterHell.DqmEntityMagematango;
import dqr.entity.mobEntity.monsterHell.DqmEntityMagemomonja;
import dqr.entity.mobEntity.monsterHell.DqmEntityMagumaron;
import dqr.entity.mobEntity.monsterHell.DqmEntityMajikaruhatto;
import dqr.entity.mobEntity.monsterHell.DqmEntityMaounokage;
import dqr.entity.mobEntity.monsterHell.DqmEntityMaporena;
import dqr.entity.mobEntity.monsterHell.DqmEntityMegazarurokku;
import dqr.entity.mobEntity.monsterHell.DqmEntityMetaruhanta;
import dqr.entity.mobEntity.monsterHell.DqmEntityMimikku;
import dqr.entity.mobEntity.monsterHell.DqmEntityPapettoman;
import dqr.entity.mobEntity.monsterHell.DqmEntityPikusi;
import dqr.entity.mobEntity.monsterHell.DqmEntityPombom;
import dqr.entity.mobEntity.monsterHell.DqmEntityPuyon;
import dqr.entity.mobEntity.monsterHell.DqmEntityRedsaikuron;
import dqr.entity.mobEntity.monsterHell.DqmEntitySaikuropusu;
import dqr.entity.mobEntity.monsterHell.DqmEntityShadopan;
import dqr.entity.mobEntity.monsterHell.DqmEntitySirubadebiru;
import dqr.entity.mobEntity.monsterHell.DqmEntitySiryou;
import dqr.entity.mobEntity.monsterHell.DqmEntitySiryounokisi;
import dqr.entity.mobEntity.monsterHell.DqmEntitySodofantomu;
import dqr.entity.mobEntity.monsterHell.DqmEntityStarkimera;
import dqr.entity.mobEntity.monsterHell.DqmEntityStonman;
import dqr.entity.mobEntity.monsterHell.DqmEntitySuraimubehomazun;
import dqr.entity.mobEntity.monsterHell.DqmEntitySuraimuhaitawa;
import dqr.entity.mobEntity.monsterHell.DqmEntitySuraimumadyura;
import dqr.entity.mobEntity.monsterHell.DqmEntityTororuking;
import dqr.entity.mobEntity.monsterHell.DqmEntityTubokku;
import dqr.entity.mobEntity.monsterHell.DqmEntityUmibouzu;
import dqr.entity.mobEntity.monsterHell.DqmEntityWhitepan2;
import dqr.entity.mobEntity.monsterHell.DqmMobBaseHell;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityDaiyamondosuraimu;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityDragometaru;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityGoldenmetalslime;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityHagumeta;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityHaguremetaruking;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityMetaking;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityMetaruburazazu;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityMetasura;
import dqr.entity.mobEntity.monsterMetaru.DqmEntityPuratinaking;
import dqr.entity.mobEntity.monsterNight.DqmEntityAkumanosyo;
import dqr.entity.mobEntity.monsterNight.DqmEntityAkumanotubo;
import dqr.entity.mobEntity.monsterNight.DqmEntityAnimaruzonbi;
import dqr.entity.mobEntity.monsterNight.DqmEntityArumiraji;
import dqr.entity.mobEntity.monsterNight.DqmEntityAxedoragon;
import dqr.entity.mobEntity.monsterNight.DqmEntityBaburin;
import dqr.entity.mobEntity.monsterNight.DqmEntityBakudanbebi;
import dqr.entity.mobEntity.monsterNight.DqmEntityBakudaniwa;
import dqr.entity.mobEntity.monsterNight.DqmEntityBebisatan;
import dqr.entity.mobEntity.monsterNight.DqmEntityBehoimisuraimu;
import dqr.entity.mobEntity.monsterNight.DqmEntityBehoimusuraimu;
import dqr.entity.mobEntity.monsterNight.DqmEntityBerobero;
import dqr.entity.mobEntity.monsterNight.DqmEntityBeronyaago;
import dqr.entity.mobEntity.monsterNight.DqmEntityBesuking;
import dqr.entity.mobEntity.monsterNight.DqmEntityBuchunpa;
import dqr.entity.mobEntity.monsterNight.DqmEntityBuraddihando;
import dqr.entity.mobEntity.monsterNight.DqmEntityButtizukinya;
import dqr.entity.mobEntity.monsterNight.DqmEntityDansunidoru;
import dqr.entity.mobEntity.monsterNight.DqmEntityDesufuratta;
import dqr.entity.mobEntity.monsterNight.DqmEntityDokuyazukin;
import dqr.entity.mobEntity.monsterNight.DqmEntityDoraki;
import dqr.entity.mobEntity.monsterNight.DqmEntityDorakima;
import dqr.entity.mobEntity.monsterNight.DqmEntityDoroningyou;
import dqr.entity.mobEntity.monsterNight.DqmEntityEnzeruslime;
import dqr.entity.mobEntity.monsterNight.DqmEntityFgizumo;
import dqr.entity.mobEntity.monsterNight.DqmEntityGaikotukensi;
import dqr.entity.mobEntity.monsterNight.DqmEntityGappurin;
import dqr.entity.mobEntity.monsterNight.DqmEntityGenjutusi;
import dqr.entity.mobEntity.monsterNight.DqmEntityGhost;
import dqr.entity.mobEntity.monsterNight.DqmEntityGorotuki;
import dqr.entity.mobEntity.monsterNight.DqmEntityHerughost;
import dqr.entity.mobEntity.monsterNight.DqmEntityHgizumo;
import dqr.entity.mobEntity.monsterNight.DqmEntityHitokuiga;
import dqr.entity.mobEntity.monsterNight.DqmEntityHoroghost;
import dqr.entity.mobEntity.monsterNight.DqmEntityHyouganmajin;
import dqr.entity.mobEntity.monsterNight.DqmEntityJeriman;
import dqr.entity.mobEntity.monsterNight.DqmEntityKingsura;
import dqr.entity.mobEntity.monsterNight.DqmEntityKirapan2;
import dqr.entity.mobEntity.monsterNight.DqmEntityKirasuko;
import dqr.entity.mobEntity.monsterNight.DqmEntityMapetman;
import dqr.entity.mobEntity.monsterNight.DqmEntityMarinsuraimu;
import dqr.entity.mobEntity.monsterNight.DqmEntityMatango;
import dqr.entity.mobEntity.monsterNight.DqmEntityMeijidoraki;
import dqr.entity.mobEntity.monsterNight.DqmEntityMeijikimera;
import dqr.entity.mobEntity.monsterNight.DqmEntityMeragosuto;
import dqr.entity.mobEntity.monsterNight.DqmEntityMetaruhantaken;
import dqr.entity.mobEntity.monsterNight.DqmEntityMetaruraida;
import dqr.entity.mobEntity.monsterNight.DqmEntityMetoroghost;
import dqr.entity.mobEntity.monsterNight.DqmEntityMinidemon;
import dqr.entity.mobEntity.monsterNight.DqmEntityMokomokojuu;
import dqr.entity.mobEntity.monsterNight.DqmEntityMrippusu;
import dqr.entity.mobEntity.monsterNight.DqmEntityNightwalker;
import dqr.entity.mobEntity.monsterNight.DqmEntityObakekyandoru;
import dqr.entity.mobEntity.monsterNight.DqmEntityOdoruhouseki;
import dqr.entity.mobEntity.monsterNight.DqmEntityPinkmomon;
import dqr.entity.mobEntity.monsterNight.DqmEntityRaimusuraimu;
import dqr.entity.mobEntity.monsterNight.DqmEntitySamayoutamasii;
import dqr.entity.mobEntity.monsterNight.DqmEntitySamayouyoroi;
import dqr.entity.mobEntity.monsterNight.DqmEntitySibireageha;
import dqr.entity.mobEntity.monsterNight.DqmEntitySibiredanbira;
import dqr.entity.mobEntity.monsterNight.DqmEntitySkullgaroo;
import dqr.entity.mobEntity.monsterNight.DqmEntitySumairurokku;
import dqr.entity.mobEntity.monsterNight.DqmEntitySumoruguru;
import dqr.entity.mobEntity.monsterNight.DqmEntitySupekutetto;
import dqr.entity.mobEntity.monsterNight.DqmEntitySura2;
import dqr.entity.mobEntity.monsterNight.DqmEntitySuraimubogu;
import dqr.entity.mobEntity.monsterNight.DqmEntitySuraimuburesu;
import dqr.entity.mobEntity.monsterNight.DqmEntitySyado;
import dqr.entity.mobEntity.monsterNight.DqmEntityTahodoraki;
import dqr.entity.mobEntity.monsterNight.DqmEntityTomosibikozou;
import dqr.entity.mobEntity.monsterNight.DqmEntityTonburero;
import dqr.entity.mobEntity.monsterNight.DqmEntityTororu;
import dqr.entity.mobEntity.monsterNight.DqmEntityTutiwarasi;
import dqr.entity.mobEntity.monsterNight.DqmEntityUmiusi;
import dqr.entity.mobEntity.monsterNight.DqmEntityYouganmajin;
import dqr.entity.mobEntity.monsterNight.DqmMobBaseNight;
import dqr.entity.mobEntity.monsterSP.DqmEntityAtorasu;
import dqr.entity.mobEntity.monsterSP.DqmEntityBazuzu;
import dqr.entity.mobEntity.monsterSP.DqmEntityBlackchack;
import dqr.entity.mobEntity.monsterSP.DqmEntityBurasu;
import dqr.entity.mobEntity.monsterSP.DqmEntityDarkRamia;
import dqr.entity.mobEntity.monsterSP.DqmEntityFureizado;
import dqr.entity.mobEntity.monsterSP.DqmEntityGodonheddo;
import dqr.entity.mobEntity.monsterSP.DqmEntityKandata;
import dqr.entity.mobEntity.monsterSP.DqmEntityKandatakobun;
import dqr.entity.mobEntity.monsterSP.DqmEntityKinghidora;
import dqr.entity.mobEntity.monsterSP.DqmEntityKiramajinga;
import dqr.entity.mobEntity.monsterSP.DqmEntityKiratoti;
import dqr.entity.mobEntity.monsterSP.DqmEntityMashougumo;
import dqr.entity.mobEntity.monsterSP.DqmEntityMasso;
import dqr.entity.mobEntity.monsterSP.DqmEntityOrutega;
import dqr.entity.mobEntity.monsterSP.DqmEntityPisaronaito;
import dqr.entity.mobEntity.monsterSP.DqmEntityPuremiasuraimu;
import dqr.entity.mobEntity.monsterSP.DqmEntitySirubamanto;
import dqr.entity.mobEntity.monsterSP.DqmEntitySuraimuemperor;
import dqr.entity.mobEntity.monsterSP.DqmEntitySuraimujeneraru;
import dqr.entity.mobEntity.monsterSP.DqmEntityTattyan;
import dqr.entity.mobEntity.monsterSP.DqmEntityTororubakkosu;
import dqr.entity.mobEntity.monsterSP.DqmEntityUragirikozou;
import dqr.entity.mobEntity.monsterSP.DqmEntityWanpakusatan;
import dqr.entity.mobEntity.monsterSP.DqmEntityYamatanooroti;
import dqr.entity.mobEntity.monsterSP.DqmMobBaseSP;
import dqr.entity.mobEntity.monsterTensei.DqmEntityArukemisuton;
import dqr.entity.mobEntity.monsterTensei.DqmEntityBebingosatan;
import dqr.entity.mobEntity.monsterTensei.DqmEntityDebirurodo;
import dqr.entity.mobEntity.monsterTensei.DqmEntityGoldmanto;
import dqr.entity.mobEntity.monsterTensei.DqmEntityGorudenkon;
import dqr.entity.mobEntity.monsterTensei.DqmEntityHatonaito;
import dqr.entity.mobEntity.monsterTensei.DqmEntityKirapike;
import dqr.entity.mobEntity.monsterTensei.DqmEntityKuinmomon;
import dqr.entity.mobEntity.monsterTensei.DqmEntityMaaburun;
import dqr.entity.mobEntity.monsterTensei.DqmEntityMadrainbow;
import dqr.entity.mobEntity.monsterTensei.DqmEntityMetaruhoimin;
import dqr.entity.mobEntity.monsterTensei.DqmEntityMomoirosansimai;
import dqr.entity.mobEntity.monsterTensei.DqmEntityMoonkimera;
import dqr.entity.mobEntity.monsterTensei.DqmEntityNoroinoiwa;
import dqr.entity.mobEntity.monsterTensei.DqmEntityPinkbonbon;
import dqr.entity.mobEntity.monsterTensei.DqmEntityReddoatya;
import dqr.entity.mobEntity.monsterTensei.DqmEntitySabotengold;
import dqr.entity.mobEntity.monsterTensei.DqmEntitySeigin;
import dqr.entity.mobEntity.monsterTensei.DqmEntityShuvaluts;
import dqr.entity.mobEntity.monsterTensei.DqmEntitySirudoaniki;
import dqr.entity.mobEntity.monsterTensei.DqmEntitySweetbag;
import dqr.entity.mobEntity.monsterTensei.DqmEntityTaipug;
import dqr.entity.mobEntity.monsterTensei.DqmEntityTogekonbou;
import dqr.entity.mobEntity.monsterTensei.DqmEntityTumurinmama;
import dqr.entity.mobEntity.monsterTensei.DqmEntityTyokonuba;
import dqr.entity.mobEntity.monsterTensei.DqmMobBaseTensei;

public class DQRespect implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        // dqr.handler.LivingDropHandler version 0.9.4.8#9-i

        // spotless:off

        if(recipe.entity instanceof EntityCow)
        {
            drops.add(new MobDrop(new ItemStack(DQMiscs.itemUsinofun), MobDrop.DropType.Normal, 3333, null, null, false, false));
        }
        else if(recipe.entity instanceof EntityHorse)
        {
            drops.add(new MobDrop(new ItemStack(DQMiscs.itemUmanofun), MobDrop.DropType.Normal, 3333, null, null, false, false));
        }

        if(recipe.entity instanceof DqmMobBase)
        {
            if(((DqmMobBase)recipe.entity).MobRoot.getId() == EnumDqmMobRoot.AKUMA.getId())
            {
                int rate = 0;
                if (!(recipe.entity instanceof DqmMobBaseDay) && !(recipe.entity instanceof DqmMobBaseNight)) {
                    if (recipe.entity instanceof DqmMobBaseHell) {
                        rate = 1000;
                    } else if (recipe.entity instanceof DqmMobBaseEnd) {
                        rate = 500;
                    } else if (recipe.entity instanceof DqmMobBaseSP) {
                        rate = 100;
                    } else if (recipe.entity instanceof DqmMobBaseTensei) {
                        rate = 100;
                    }
                } else {
                    rate = 3000;
                }
                if(rate > 0)
                {
                    drops.add(new MobDrop(new ItemStack(DQAccessories.itemAkumanopiasu), MobDrop.DropType.Normal, (int)((1d / (double)rate) * 10000d), null, null, false, false));
                }
            }
            if(((DqmMobBase)recipe.entity).MobRoot.getId() == EnumDqmMobRoot.DRAGON.getId())
            {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDragon), MobDrop.DropType.Normal, 10, null, null, false, false));
                drops.add(new MobDrop(new ItemStack(DQMiscs.itemDragonObuB), MobDrop.DropType.Normal, 3, null, null, false, false));
            }
        }
        if(recipe.entity instanceof DqmMobBaseBoss)
        {
            drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbHero), MobDrop.DropType.Normal, 500, null, null, false, false));
        }
        if(recipe.entity instanceof DqmMobBaseTensei)
        {
            if(recipe.entity instanceof DqmEntitySeigin || recipe.entity instanceof DqmEntityGorudenkon || recipe.entity instanceof DqmEntityMoonkimera || recipe.entity instanceof DqmEntityTaipug || recipe.entity instanceof DqmEntityMetaruhoimin)
                drops.add(new MobDrop(new ItemStack(DQAccessories.itemMangetunoring), MobDrop.DropType.Normal, 20, null, null, false, false));
            else if(recipe.entity instanceof DqmEntityDebirurodo || recipe.entity instanceof DqmEntityBebingosatan || recipe.entity instanceof DqmEntityHatonaito || recipe.entity instanceof DqmEntityMomoirosansimai || recipe.entity instanceof DqmEntityPinkbonbon || recipe.entity instanceof DqmEntitySabotengold)
                drops.add(new MobDrop(new ItemStack(DQAccessories.itemHagennoring), MobDrop.DropType.Normal, 20, null, null, false, false));
            else if(recipe.entity instanceof DqmEntityMaaburun || recipe.entity instanceof DqmEntityArukemisuton || recipe.entity instanceof DqmEntityKuinmomon || recipe.entity instanceof DqmEntityGoldmanto || recipe.entity instanceof DqmEntityMadrainbow || recipe.entity instanceof DqmEntityShuvaluts)
                drops.add(new MobDrop(new ItemStack(DQAccessories.itemRiseinoring), MobDrop.DropType.Normal, 20, null, null, false, false));
            else if(recipe.entity instanceof DqmEntityTyokonuba || recipe.entity instanceof DqmEntityReddoatya || recipe.entity instanceof DqmEntityKirapike || recipe.entity instanceof DqmEntityTogekonbou || recipe.entity instanceof DqmEntityNoroinoiwa || recipe.entity instanceof DqmEntitySirudoaniki)
                drops.add(new MobDrop(new ItemStack(DQAccessories.itemHadokunoring), MobDrop.DropType.Normal, 20, null, null, false, false));
        }
        if(recipe.entity instanceof DqmEntitySweetbag)
        {
            drops.add(new MobDrop(new ItemStack(DQAccessories.itemHadokunoring), MobDrop.DropType.Normal, 2, null, null, false, false));
            drops.add(new MobDrop(new ItemStack(DQAccessories.itemHagennoring), MobDrop.DropType.Normal, 2, null, null, false, false));
            drops.add(new MobDrop(new ItemStack(DQAccessories.itemRiseinoring), MobDrop.DropType.Normal, 2, null, null, false, false));
            drops.add(new MobDrop(new ItemStack(DQAccessories.itemMangetunoring), MobDrop.DropType.Normal, 2, null, null, false, false));
        }
        if(recipe.entity instanceof DqmMobBaseTensei || recipe.entity instanceof DqmMobBaseSP)
        {
            if (recipe.entity instanceof DqmEntityWanpakusatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiratoti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMashougumo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubamanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkRamia) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodonheddo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFunanori), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAtorasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYamatanooroti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBazuzu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFunanori), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramajinga) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimujeneraru) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandatakobun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKinghidora) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasso) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMerchant), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPisaronaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuremiasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMerchant), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTattyan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFunanori), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubakkosu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUragirikozou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbHituzikai), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureizado) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandata) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBlackchack) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbHituzikai), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuemperor) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOrutega) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySeigin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbHituzikai), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDebirurodo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaaburun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArukemisuton) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudenkon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinmomon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMoonkimera) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagician), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTyokonuba) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityReddoatya) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapike) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFunanori), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogekonbou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTaipug) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebingosatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldmanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMerchant), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHatonaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadrainbow) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbHituzikai), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhoimin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomoirosansimai) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFighter), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNoroinoiwa) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbFunanori), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkbonbon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbCivilian), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotengold) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMerchant), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShuvaluts) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbWarrior), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudoaniki) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbThief), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySweetbag) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMerchant), MobDrop.DropType.Normal, 1000, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTumurinmama) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPriest), MobDrop.DropType.Normal, 1000, null, null, false, false));
            }

            if (recipe.entity instanceof DqmEntityWanpakusatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiratoti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMashougumo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubamanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkRamia) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodonheddo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAtorasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYamatanooroti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBazuzu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramajinga) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimujeneraru) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandatakobun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKinghidora) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasso) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPisaronaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuremiasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTattyan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubakkosu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUragirikozou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureizado) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandata) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBlackchack) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuemperor) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOrutega) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySeigin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDebirurodo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaaburun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArukemisuton) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudenkon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinmomon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMoonkimera) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSage), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTyokonuba) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityReddoatya) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbRanger), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapike) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogekonbou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTaipug) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebingosatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldmanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHatonaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPaladin), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadrainbow) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhoimin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomoirosansimai) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNoroinoiwa) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkbonbon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotengold) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbPirate), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShuvaluts) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMagickKnight), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudoaniki) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbBattleMaster), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySweetbag) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDancer), MobDrop.DropType.Normal, 100, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTumurinmama) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbMonsterTamer), MobDrop.DropType.Normal, 100, null, null, false, false));
            }

            if (recipe.entity instanceof DqmEntityWanpakusatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiratoti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMashougumo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubamanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkRamia) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodonheddo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAtorasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYamatanooroti) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBazuzu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramajinga) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimujeneraru) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurasu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandatakobun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKinghidora) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasso) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPisaronaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuremiasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTattyan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubakkosu) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUragirikozou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureizado) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandata) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBlackchack) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuemperor) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOrutega) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySeigin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDebirurodo) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaaburun) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArukemisuton) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudenkon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinmomon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMoonkimera) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTyokonuba) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityReddoatya) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapike) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogekonbou) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTaipug) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebingosatan) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldmanto) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHatonaito) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadrainbow) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhoimin) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomoirosansimai) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNoroinoiwa) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbTentiraimeishi), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkbonbon) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotengold) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShuvaluts) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudoaniki) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbGodHnad), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySweetbag) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbDougutukai), MobDrop.DropType.Normal, 20, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTumurinmama) {
                drops.add(new MobDrop(new ItemStack(DQEmblems.itemEmbSuperStar), MobDrop.DropType.Normal, 20, null, null, false, false));
            }

            drops.add(new MobDrop(new ItemStack(DQDecorates.DqmBlockRotomon), MobDrop.DropType.Normal, 20, null, null, false, false));
        }
        else if(recipe.entity instanceof DqmMobBaseBoss)
        {
            drops.add(new MobDrop(new ItemStack(DQDecorates.DqmBlockRotomon), MobDrop.DropType.Normal, 100, null, null, false, false));
        }

        if(recipe.entity instanceof DqmMobBase)
        {
            if (recipe.entity instanceof DqmEntityAkairai) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAkairai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAkumanosyo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAkumanosyo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAkumanotubo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAkumanotubo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAnimaruzonbi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAnimaruzonbi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAnkokumajin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAnkokumajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAroinpu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAroinpu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArukemisuton) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureArukemisuton), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArumiraji) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureArumiraji), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAtorasu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAtorasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAxedoragon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAxedoragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAyasiikage) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureAyasiikage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBaburin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBaburin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBaburuking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBaburuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBakudanbebi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBakudanbebi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBakudaniwa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBakudaniwa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBarakku) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBarakku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBariidodog) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBariidodog), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBassaimasin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBassaimasin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBatorurex) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBatorurex), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBazuzu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBazuzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebingosatan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBebingosatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebisatan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBebisatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehoimisuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBehoimisuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehoimusuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBehoimusuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehomasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBehomasuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBerobero) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBerobero), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBeronyaago) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBeronyaago), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBesuking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBesuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBigCrow) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBigCrow), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBiggufeisu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBiggufeisu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBigguhatto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBigguhatto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBiggumoai) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBiggumoai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBighanma) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBighanma), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBosutororu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBosutororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBoureikensi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBoureikensi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBubsura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBubsura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuchunpa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBuchunpa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuraddihando) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBuraddihando), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuraddosodo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBuraddosodo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurakkubejita) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBurakkubejita), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurasu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBurasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurauni) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBurauni), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurizado) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBurizado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityButisuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureButisuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityButtizukinya) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureButtizukinya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDaiyamondosuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDaiyamondosuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDakuhobitto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDakuhobitto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDakunaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDakunaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDansunidoru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDansunidoru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkRamia) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDarkRamia), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkslime) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDarkslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarktororu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDarktororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDasudragon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDasudragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDebirurodo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDebirurodo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDeddopekka) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDeddopekka), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDenga) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDenga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesufuratta) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesufuratta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesujakkaru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesujakkaru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesunyago) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesunyago), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro1) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesupisaro1), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesupisaro2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro3) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesupisaro3), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesusutoka) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDesusutoka), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDokuroarai) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDokuroarai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDokuyazukin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDokuyazukin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDollmaster) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDollmaster), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoragonsoruja) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDoragonsoruja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDorakima) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDorakima), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoroningyou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDoroningyou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoronuba) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDoronuba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDorozara) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDorozara), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoruido) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDoruido), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDqmdragon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDqmdragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragometaru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDragometaru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragondarknaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDragondarknaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragonnaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDragonnaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragonraida) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDragonraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragosuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDragosuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDucksbill) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDucksbill), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEbiruapple) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureEbiruapple), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEnzeruslime) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureEnzeruslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEriminator) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureEriminator), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEsterk) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureEsterk), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFaratto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureFaratto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureFgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureFureimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureizado) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureFureizado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFurosutogizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureFurosutogizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGaikotu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGaikotu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGaikotukensi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGaikotukensi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGamegon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegonload) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGamegonload), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegonrejendo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGamegonrejendo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGanirasu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGanirasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGappurin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGappurin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGenjutusi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGenjutusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGhost) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGhost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGigantesu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGigantesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGizumoAZ) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGizumoAZ), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGod) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGod), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodonheddo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGodonheddo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodraida) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGodraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldenmetalslime) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGoldenmetalslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldman) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGoldman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldmanto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGoldmanto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoremu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGoremu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorotuki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGorotuki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudenkon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGorudenkon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudensuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGorudensuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudentotemu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGorudentotemu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGuntaigani) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureGuntaigani), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHagumeta) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHagumeta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHaguremetaruking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHaguremetaruking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHatonaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHatonaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHerughost) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHerughost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHiitogizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHiitogizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuibako) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHitokuibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuiga) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHitokuiga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuikibako) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHitokuikibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuisaberu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHitokuisaberu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHoimisura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHoimisura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHoroghost) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHoroghost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHotatewarabi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHotatewarabi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHyouganmajin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureHyouganmajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityIkkakuusagi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureIkkakuusagi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityItamogu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureItamogu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJeriman) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureJeriman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJigokunohasami) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureJigokunohasami), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJigokunoyoroi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureJigokunoyoroi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKagenokisi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKagenokisi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandatakobun) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKandatakobun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKedamon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKedamon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKemunkurusu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKemunkurusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKimendousi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKimendousi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKimera) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKingbesu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKingbesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKinghidora) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKinghidora), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKingsura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKingsura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiraama) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKiraama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirakurabu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirakurabu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramajinga) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKiramajinga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramasin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKiramasin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramasin2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKiramasin2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirapan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapan2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirapan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapike) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirapike), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirasuko) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirasuko), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiratoti) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKiratoti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirikabuobake) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKirikabuobake), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKisudragon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKisudragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinmomon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKuinmomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKuinsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaaburun) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMaaburun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadohando) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMadohando), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadrainbow) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMadrainbow), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagematango) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMagematango), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagemomonja) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMagemomonja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagumaron) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMagumaron), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMajikaruhatto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMajikaruhatto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityManemane) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureManemane), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaounokage) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMaounokage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMapetman) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMapetman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaporena) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMaporena), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMarinsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMarinsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMashougumo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMashougumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasso) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMasso), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasterdoragon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMasterdoragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMatango) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMatango), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMegazarurokku) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMegazarurokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeijidoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMeijidoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeijikimera) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMeijikimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeragosuto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMeragosuto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruburazazu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaruburazazu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhanta) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaruhanta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhantaken) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaruhantaken), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhoimin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaruhoimin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruraida) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetaruraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetasura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetasura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetoroghost) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMetoroghost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMimikku) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMimikku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMimikkukibako) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMimikkukibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMinidemon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMinidemon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMokomokojuu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMokomokojuu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomoirosansimai) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMomoirosansimai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomonja) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMomonja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMoonkimera) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMoonkimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMrippusu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureMrippusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNightwalker) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureNightwalker), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNoroinoiwa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureNoroinoiwa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNorowaretaturugi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureNorowaretaturugi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakekinoko) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureObakekinoko), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakekyandoru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureObakekyandoru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakeumiusi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureObakeumiusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOdoruhouseki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOdoruhouseki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOnikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOnikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOokiduti) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOokiduti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOokutibasi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOokutibasi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOomedama) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOomedama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOonamekuji) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOonamekuji), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPandorabox) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePandorabox), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPandorakibako) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePandorakibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPapetkozou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePapetkozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPapettoman) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePapettoman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPikusi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePikusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkbonbon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePinkbonbon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkmomon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePinkmomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPisaronaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePisaronaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPombom) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePombom), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuratinaking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePuratinaking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuremiasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePuremiasuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPurizunyan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePurizunyan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuyon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigurePuyon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRaimusuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRaimusuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityReddoatya) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureReddoatya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRedsaikuron) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRedsaikuron), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRemonsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRemonsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRippusu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRippusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRiripat) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRiripat), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuiso) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRyuiso), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuuou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRyuuou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuuou2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureRyuuou2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotenboru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSabotenboru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotengold) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSabotengold), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySaikuropusu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSaikuropusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySamayoutamasii) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSamayoutamasii), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySamayouyoroi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSamayouyoroi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySeigin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSeigin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShuvaluts) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureShuvaluts), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibireageha) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSibireageha), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibiredanbira) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSibiredanbira), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibirekurage) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSibirekurage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySimasimacat) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSimasimacat), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubadebiru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSirubadebiru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubamanto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSirubamanto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudoaniki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSirudoaniki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudokozou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSirudokozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySiryou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSiryou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySiryounokisi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSiryounokisi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySkullgaroo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSkullgaroo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySodofantomu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSodofantomu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityStarkimera) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureStarkimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityStonman) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureStonman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySukippaa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSukippaa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySumairurokku) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSumairurokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySumoruguru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSumoruguru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySunomon) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSunomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySupekutetto) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSupekutetto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySupini) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSupini), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySura2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSura2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubehomazun) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimubehomazun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubesu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimubesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubogu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimubogu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuburesu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimuburesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuhaitawa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimuhaitawa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimujeneraru) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimujeneraru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimumadyura) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimumadyura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimunaito) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimunaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimutawa) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimutawa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimutumuri) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimutumuri), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySweetbag) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSweetbag), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySyado) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSyado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTahodoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTahodoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTaipug) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTaipug), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTattyan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTattyan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogebouzu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTogebouzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogekonbou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTogekonbou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTomosibikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTomosibikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTonburero) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTonburero), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubakkosu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTororubakkosu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubonba) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTororubonba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororuking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTororuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTubo) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTubo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTubokku) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTubokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTukaima) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTukaima), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTumurinmama) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTumurinmama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTutiwarasi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTutiwarasi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTyokonuba) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureTyokonuba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUmibouzu) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureUmibouzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUmiusi) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureUmiusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUragirikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureUragirikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUzusioking) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureUzusioking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWanpakusatan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureWanpakusatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWaraibukuro) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureWaraibukuro), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYamatanooroti) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureYamatanooroti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYouganmajin) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureYouganmajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZinmentyou) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureZinmentyou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZoma) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureZoma), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZukkinya) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureZukkinya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandata) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureKandata), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBlackchack) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureBlackchack), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuemperor) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureSuraimuemperor), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkdoriado) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureDarkdoriado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShadopan) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureShadopan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShadopan2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureShadopan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOrutega) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureOrutega), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWhitepan2) {
                drops.add(new MobDrop(new ItemStack(DQMobFigures.BlockFigureWhitepan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            }

            if (recipe.entity instanceof DqmEntityAkairai) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAkairai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAkumanosyo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAkumanosyo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAkumanotubo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAkumanotubo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAnimaruzonbi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAnimaruzonbi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAnkokumajin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAnkokumajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAroinpu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAroinpu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArukemisuton) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjArukemisuton), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityArumiraji) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjArumiraji), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAtorasu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAtorasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAxedoragon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAxedoragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityAyasiikage) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjAyasiikage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBaburin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBaburin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBaburuking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBaburuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBakudanbebi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBakudanbebi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBakudaniwa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBakudaniwa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBarakku) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBarakku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBariidodog) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBariidodog), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBassaimasin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBassaimasin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBatorurex) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBatorurex), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBazuzu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBazuzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebingosatan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBebingosatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBebisatan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBebisatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehoimisuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBehoimisuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehoimusuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBehoimusuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBehomasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBehomasuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBerobero) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBerobero), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBeronyaago) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBeronyaago), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBesuking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBesuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBigCrow) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBigCrow), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBiggufeisu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBiggufeisu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBigguhatto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBigguhatto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBiggumoai) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBiggumoai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBighanma) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBighanma), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBosutororu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBosutororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBoureikensi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBoureikensi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBubsura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBubsura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuchunpa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBuchunpa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuraddihando) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBuraddihando), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBuraddosodo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBuraddosodo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurakkubejita) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBurakkubejita), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurasu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBurasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurauni) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBurauni), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBurizado) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBurizado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityButisuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjButisuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityButtizukinya) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjButtizukinya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDaiyamondosuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDaiyamondosuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDakuhobitto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDakuhobitto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDakunaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDakunaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDansunidoru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDansunidoru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkRamia) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDarkRamia), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkslime) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDarkslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarktororu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDarktororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDasudragon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDasudragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDebirurodo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDebirurodo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDeddopekka) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDeddopekka), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDenga) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDenga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesufuratta) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesufuratta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesujakkaru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesujakkaru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesunyago) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesunyago), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro1) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesupisaro1), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesupisaro2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesupisaro3) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesupisaro3), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDesusutoka) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDesusutoka), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDokuroarai) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDokuroarai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDokuyazukin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDokuyazukin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDollmaster) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDollmaster), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoragonsoruja) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDoragonsoruja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDorakima) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDorakima), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoroningyou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDoroningyou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoronuba) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDoronuba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDorozara) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDorozara), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDoruido) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDoruido), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDqmdragon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDqmdragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragometaru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDragometaru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragondarknaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDragondarknaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragonnaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDragonnaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragonraida) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDragonraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDragosuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDragosuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDucksbill) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDucksbill), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEbiruapple) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjEbiruapple), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEnzeruslime) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjEnzeruslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEriminator) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjEriminator), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityEsterk) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjEsterk), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFaratto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjFaratto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjFgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjFureimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFureizado) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjFureizado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityFurosutogizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjFurosutogizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGaikotu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGaikotu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGaikotukensi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGaikotukensi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGamegon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegonload) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGamegonload), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGamegonrejendo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGamegonrejendo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGanirasu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGanirasu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGappurin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGappurin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGenjutusi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGenjutusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGhost) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGhost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGigantesu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGigantesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGizumoAZ) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGizumoAZ), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGod) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGod), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodonheddo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGodonheddo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGodraida) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGodraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldenmetalslime) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGoldenmetalslime), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldman) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGoldman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoldmanto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGoldmanto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGoremu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGoremu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorotuki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGorotuki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudenkon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGorudenkon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudensuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGorudensuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGorudentotemu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGorudentotemu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityGuntaigani) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjGuntaigani), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHagumeta) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHagumeta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHaguremetaruking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHaguremetaruking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHatonaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHatonaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHerughost) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHerughost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHgizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHgizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHiitogizumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHiitogizumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuibako) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHitokuibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuiga) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHitokuiga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuikibako) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHitokuikibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHitokuisaberu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHitokuisaberu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHoimisura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHoimisura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHoroghost) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHoroghost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHotatewarabi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHotatewarabi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityHyouganmajin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjHyouganmajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityIkkakuusagi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjIkkakuusagi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityItamogu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjItamogu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJeriman) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjJeriman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJigokunohasami) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjJigokunohasami), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityJigokunoyoroi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjJigokunoyoroi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKagenokisi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKagenokisi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandatakobun) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKandatakobun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKedamon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKedamon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKemunkurusu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKemunkurusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKimendousi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKimendousi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKimera) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKingbesu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKingbesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKinghidora) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKinghidora), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKingsura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKingsura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiraama) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKiraama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirakurabu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirakurabu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramajinga) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKiramajinga), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramasin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKiramasin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiramasin2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKiramasin2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirapan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapan2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirapan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirapike) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirapike), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirasuko) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirasuko), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKiratoti) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKiratoti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKirikabuobake) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKirikabuobake), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKisudragon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKisudragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinmomon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKuinmomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKuinsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKuinsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaaburun) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMaaburun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadohando) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMadohando), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMadrainbow) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMadrainbow), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagematango) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMagematango), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagemomonja) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMagemomonja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMagumaron) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMagumaron), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMajikaruhatto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMajikaruhatto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityManemane) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjManemane), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaounokage) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMaounokage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMapetman) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMapetman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMaporena) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMaporena), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMarinsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMarinsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMashougumo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMashougumo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasso) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMasso), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMasterdoragon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMasterdoragon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMatango) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMatango), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMegazarurokku) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMegazarurokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeijidoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMeijidoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeijikimera) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMeijikimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMeragosuto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMeragosuto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruburazazu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaruburazazu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhanta) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaruhanta), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhantaken) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaruhantaken), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruhoimin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaruhoimin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetaruraida) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetaruraida), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetasura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetasura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMetoroghost) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMetoroghost), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMimikku) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMimikku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMimikkukibako) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMimikkukibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMinidemon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMinidemon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMokomokojuu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMokomokojuu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomoirosansimai) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMomoirosansimai), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMomonja) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMomonja), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMoonkimera) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMoonkimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityMrippusu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjMrippusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNightwalker) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjNightwalker), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNoroinoiwa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjNoroinoiwa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityNorowaretaturugi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjNorowaretaturugi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakekinoko) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjObakekinoko), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakekyandoru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjObakekyandoru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityObakeumiusi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjObakeumiusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOdoruhouseki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOdoruhouseki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOnikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOnikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOokiduti) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOokiduti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOokutibasi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOokutibasi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOomedama) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOomedama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOonamekuji) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOonamekuji), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPandorabox) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPandorabox), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPandorakibako) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPandorakibako), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPapetkozou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPapetkozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPapettoman) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPapettoman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPikusi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPikusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkbonbon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPinkbonbon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPinkmomon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPinkmomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPisaronaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPisaronaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPombom) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPombom), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuratinaking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPuratinaking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuremiasuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPuremiasuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPurizunyan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPurizunyan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityPuyon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjPuyon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRaimusuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRaimusuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityReddoatya) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjReddoatya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRedsaikuron) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRedsaikuron), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRemonsuraimu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRemonsuraimu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRippusu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRippusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRiripat) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRiripat), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuiso) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRyuiso), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuuou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRyuuou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityRyuuou2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjRyuuou2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotenboru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSabotenboru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySabotengold) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSabotengold), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySaikuropusu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSaikuropusu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySamayoutamasii) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSamayoutamasii), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySamayouyoroi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSamayouyoroi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySeigin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSeigin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShuvaluts) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjShuvaluts), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibireageha) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSibireageha), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibiredanbira) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSibiredanbira), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySibirekurage) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSibirekurage), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySimasimacat) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSimasimacat), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubadebiru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSirubadebiru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirubamanto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSirubamanto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudoaniki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSirudoaniki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySirudokozou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSirudokozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySiryou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSiryou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySiryounokisi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSiryounokisi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySkullgaroo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSkullgaroo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySodofantomu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSodofantomu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityStarkimera) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjStarkimera), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityStonman) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjStonman), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySukippaa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSukippaa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySumairurokku) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSumairurokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySumoruguru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSumoruguru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySunomon) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSunomon), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySupekutetto) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSupekutetto), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySupini) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSupini), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySura2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSura2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubehomazun) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimubehomazun), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubesu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimubesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimubogu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimubogu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuburesu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimuburesu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuhaitawa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimuhaitawa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimujeneraru) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimujeneraru), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimumadyura) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimumadyura), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimunaito) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimunaito), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimutawa) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimutawa), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimutumuri) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimutumuri), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySweetbag) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSweetbag), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySyado) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSyado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTahodoraki) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTahodoraki), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTaipug) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTaipug), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTattyan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTattyan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogebouzu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTogebouzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTogekonbou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTogekonbou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTomosibikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTomosibikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTonburero) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTonburero), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTororu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubakkosu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTororubakkosu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororubonba) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTororubonba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTororuking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTororuking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTubo) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTubo), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTubokku) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTubokku), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTukaima) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTukaima), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTumurinmama) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTumurinmama), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTutiwarasi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTutiwarasi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityTyokonuba) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjTyokonuba), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUmibouzu) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjUmibouzu), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUmiusi) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjUmiusi), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUragirikozou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjUragirikozou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityUzusioking) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjUzusioking), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWanpakusatan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjWanpakusatan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWaraibukuro) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjWaraibukuro), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYamatanooroti) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjYamatanooroti), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityYouganmajin) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjYouganmajin), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZinmentyou) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjZinmentyou), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZoma) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjZoma), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityZukkinya) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjZukkinya), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityKandata) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjKandata), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityBlackchack) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjBlackchack), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntitySuraimuemperor) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjSuraimuemperor), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityDarkdoriado) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjDarkdoriado), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShadopan) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjShadopan), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityShadopan2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjShadopan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityOrutega) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjOrutega), MobDrop.DropType.Normal, 166, null, null, false, false));
            } else if (recipe.entity instanceof DqmEntityWhitepan2) {
                drops.add(new MobDrop(new ItemStack(DQMobObjects.BlockObjWhitepan2), MobDrop.DropType.Normal, 166, null, null, false, false));
            }
        }

        // spotless:on

        // dqr.handler.LivingEndoraHandler.onDeathDropHookEvent

        if (recipe.entity instanceof EntityDragon) {
            drops.add(
                new MobDrop(
                    new ItemStack(DQMiscs.itemAtkEndora),
                    MobDrop.DropType.Normal,
                    10000,
                    null,
                    null,
                    false,
                    false));
        }
    }

}
