/*
 * This code may be freely redistributed under the 
 * terms of the GPL
 *
 * James McCarty 
 *
 * 2012
 */

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
/* #include <malloc.h> */
#include <malloc.h>
#include <pthread.h>
/* #include <libtiff/tiffio.h> */
#include <string.h>
#include <time.h>
#include "fractal.h"
// #include "inc/tiffio.h"
#include "jpeglib.h"

#define RANDR(lo, hi) ((lo) + (((hi)-(lo)) * drand48()))

int random_bit (void) {
    return random () & 01;
}

/* from Paul Bourke */
void ContractiveMapping (coeff * coeff) {
    double a, b, d, e;

    do {
        do {
            a = drand48 ();
            d = RANDR (a * a, 1);
            if (random_bit ()) d = -d;
        } while ((a * a + d * d) > 1);

        do {
            b = drand48 ();
            e = RANDR (b * b, 1);
            if (random_bit ())e = -e;
        } while ((b * b + e * e) > 1);
    } while ((a * a + b * b + d * d + e * e) > (1 + (a * e - d * b) * (a * e - d * b)));

    coeff->ac = a;
    coeff->bc = b;
    coeff->cc = RANDR (-2, 2);
    coeff->dc = d;
    coeff->ec = e;
    coeff->fc = RANDR (-2, 2);
}

/* initialize the coefficient values */
void coeff_init (flame *fractal) {
    int i;
    int file_r, file_g, file_b;
    double fa, fb, fc, fd, fe, ff;

    fractal->coarray = malloc (fractal->n * sizeof (coeff));
    if (fractal->coarray == NULL) {
        // printf ("Error: malloc() failed in buffer_init(). Tried to malloc %d * %ld bytes.\n", fractal->n, (long) sizeof (coeff));
        LOGES("coeff_init", "Error: malloc() failed\n");
        exit (EXIT_FAILURE);
    }

    for (i = 0; i < fractal->n; i++) {
        if (random_bit ())ContractiveMapping (&(fractal->coarray[i])); else {
            fractal->coarray[i].ac = RANDR (-1.5, 1.5);
            fractal->coarray[i].bc = RANDR (-1.5, 1.5);
            fractal->coarray[i].cc = RANDR (-1.5, 1.5);
            fractal->coarray[i].dc = RANDR (-1.5, 1.5);
            fractal->coarray[i].ec = RANDR (-1.5, 1.5);
            fractal->coarray[i].fc = RANDR (-1.5, 1.5);
        }

        fractal->coarray[i].pa1 = RANDR (-2, 2);
        fractal->coarray[i].pa2 = RANDR (-2, 2);
        fractal->coarray[i].pa3 = RANDR (-2, 2);
        fractal->coarray[i].pa4 = RANDR (-2, 2);

        fractal->coarray[i].r =
                fractal->R !=
                -1 ? (unsigned char) fractal->R : (unsigned char) (64 +
                                                                   RANDR (64, 256));
        fractal->coarray[i].g =
                fractal->G !=
                -1 ? (unsigned char) fractal->G : (unsigned char) (64 +
                                                                   RANDR (64, 256));
        fractal->coarray[i].b =
                fractal->B !=
                -1 ? (unsigned char) fractal->B : (unsigned char) (64 +
                                                                   RANDR (64, 256));
    }

    if (fractal->pallete != NULL) {
        i = 0;
        while ((fscanf
                        (fractal->pallete, "%d %d %d\n", &file_r, &file_g,
                         &file_b) != EOF) && i < fractal->n) {

            fractal->coarray[i].r = (unsigned char) file_r;
            fractal->coarray[i].g = (unsigned char) file_g;
            fractal->coarray[i].b = (unsigned char) file_b;
            //printf ("Setting index %d to %d,%d,%d.\n", i, file_r, file_g,
            //        file_b);
            i++;
        }
        (void) fclose (fractal->pallete);
    }

    if (fractal->cofile != NULL) {
        i = 0;
        while ((fscanf (fractal->cofile, "%lf %lf %lf %lf %lf %lf\n",
                        &fa, &fb, &fc, &fd, &fe, &ff) != EOF) && i < fractal->n) {
            fractal->coarray[i].ac = fa;
            fractal->coarray[i].bc = fb;
            fractal->coarray[i].cc = fc;
            fractal->coarray[i].dc = fd;
            fractal->coarray[i].ec = fe;
            fractal->coarray[i].fc = ff;
            // printf ("Setting index coeffs at index %d\n", i);
            i++;
        }
        (void) fclose (fractal->cofile);
    }

    for (i = 0; i < fractal->n; i++) {
        printf ("%f %f %f %f %f %f\n",
                fractal->coarray[i].ac,
                fractal->coarray[i].bc,
                fractal->coarray[i].cc,
                fractal->coarray[i].dc,
                fractal->coarray[i].ec, fractal->coarray[i].fc);
    }

}

/* takes a flame structure and sets all initial values. */
void
fractal_init (flame *fractal) {

    /* change default values set in fractal.h */

    fractal->xres = XRES;
    fractal->yres = YRES;
    fractal->xmax = XMAX;
    fractal->ymax = YMAX;
    fractal->xmin = XMIN;
    fractal->ymin = YMIN;
    fractal->n = NUMV;
    fractal->seed = SEED;
    fractal->samples = SAMPLES;
    fractal->gamma = GAMMA;
    fractal->symmetry = 1;
    fractal->invert = 0;
    fractal->file = PATH;
    fractal->sup = SUPER;
    fractal->pallete = NULL;
    fractal->cofile = NULL;
    fractal->iterations = ITT;
    fractal->num_threads = THREADGROUPSIZE;
    fractal->R = -1;
    fractal->G = -1;
    fractal->B = -1;
    fractal->count = 1;
    fractal->coarray = NULL;
    fractal->choice = malloc (fractal->count * sizeof (int));
    if (fractal->choice == NULL) {
        // printf
        //          ("Error: malloc() failed in fractal_init.  Tried to malloc %d * %ld bytes.\n",
        //          fractal->count, sizeof (int));
                 LOGES("fractal_init", "Error: malloc() failed\n");
        exit (EXIT_FAILURE);
    }
    fractal->choice[0] = 0;
}

/* allocate memory for the image buffer */
void
buffer_init (flame *fractal) {

    int y;
    /* last minute sanity checks */
    fractal->xres = fractal->xres <= 0 ? XRES : fractal->xres;
    fractal->yres = fractal->yres <= 0 ? YRES : fractal->yres;

    if (fractal->xmin > fractal->xmax) {
        double t = fractal->xmin;
        fractal->xmin = fractal->xmax;
        fractal->xmax = t;
    }

    if (fractal->ymin > fractal->ymax) {
        double t = fractal->ymin;
        fractal->ymin = fractal->ymax;
        fractal->ymax = t;
    }

    fractal->ranx = fractal->xmax - fractal->xmin;
    fractal->rany = fractal->ymax - fractal->ymin;

    fractal->xres *= fractal->sup;
    fractal->yres *= fractal->sup;

    /* malloc new memory array for the image */
    /* then memory can be freed after being written to disk */

    //printf ("Size of pixel structure is %ld bytes.\n", sizeof (pixel));
    //printf ("Attempting to allocate %ld MiB of RAM.\n",
    //        ((fractal->yres * fractal->xres * sizeof (pixel))) / (1024 * 1024));

    fractal->pixels = malloc (fractal->yres * sizeof (pixel *));
    if (fractal->pixels == NULL) {
        // printf ("malloc() in buffer_init () failed.\n");
        LOGES("buffer_init", "Error: malloc() failed\n");
        exit (EXIT_FAILURE);
    }
    fractal->lock = malloc (fractal->yres * sizeof (pthread_mutex_t));
    if (fractal->lock == NULL) {
        // printf ("malloc() in buffer_init () failed.\n");
        LOGES("buffer_init", "Error: malloc() failed\n");
        exit (EXIT_FAILURE);
    }
    for (y = 0; y < fractal->yres; y++) {
        fractal->pixels[y] = malloc (fractal->xres * sizeof (pixel));
        if (fractal->pixels[y] == NULL) {
            // printf ("malloc() failed\n");
            LOGES("buffer_init", "Error: malloc() failed\n");
            exit (EXIT_FAILURE);
        }
        memset (fractal->pixels[y], '\0', fractal->xres * sizeof (pixel));
        if (0 != pthread_mutex_init (&(fractal->lock[y]), NULL)) {
            // printf ("Error: mutex init failed.\n");
            LOGES("buffer_init", "Error: malloc() failed\n");
        }
    }

    // printf ("Done!\n");
    LOGIS("buffer_init", "Done!\n");
}

void
print_usage () {
    /* print program use */

    printf ("fractal usage:\n");
    printf ("fractal [-options ...]\n\n");
    printf ("options include:\n");

    printf ("\t-h\t\t\tprint this screen\n");
    printf ("\t-R NUM\t\t\tseed randomizer with NUM\n");
    printf ("\t-f NAME [%s]\tfile to write\n", PATH);
    printf ("\t-S NUM>1 [1]\t\tenable rotational symmetry axis\n");
    printf ("\t-I\t\t\tinvert colors in final image\n");
    printf ("\t-x XRES [%d]\t\timage x resolution\n", XRES);
    printf ("\t-y YRES [%d]\t\timage y resolution\n", YRES);
    printf ("\t-m XMIN [%f]\tgraph x minimum\n", (float) XMIN);
    printf ("\t-M XMAX [%f]\tgraph x maximum\n", (float) XMAX);
    printf ("\t-l YMIN [%f]\tgraph y minimum\n", (float) YMIN);
    printf ("\t-L YMAX [%f]\tgraph y maximum\n", (float) YMAX);
    printf ("\t-n NUMV [%d]\t\tnumber of random vectors to use\n", NUMV);
    printf ("\t-s SAMPLES [%d]\tnumber of image samples\n", SAMPLES);
    printf ("\t-i NUM>20 [%d]\tnumber of iterations per sample\n", ITT);
    printf ("\t-r 0<=NUM<=255\t\tset static RED channel value\n");
    printf ("\t-g 0<=NUM<=255\t\tset static GREEN channel value\n");
    printf ("\t-b 0<=NUM<=255\t\tset static BLUE channel value\n");
    printf ("\t-sup NUM [%d] \t\tsuper sample NUM^2 bit buckets\n", SUPER);
    printf ("\t-G NUM [%f]\tcorrectional gamma factor\n", (float) GAMMA);
    printf ("\t-p FILE\t\t\tuse input color pallete\n");
    printf ("\t-c FILE\t\t\tuse coefficient input file\n");
    printf ("\t-T THREADS [%d]\tnumber of threads to run\n", THREADGROUPSIZE);
    printf ("\t-v NUM\t\t\tuse equation by number: see below\n");

    printf ("\n\nValues for v include:\n");
    printf ("\t0\t\t\tLinear\n");
    printf ("\t1\t\t\tSinusoidal\n");
    printf ("\t2\t\t\tSpherical\n");
    printf ("\t3\t\t\tSwirl\n");
    printf ("\t4\t\t\tHorseshoe\n");
    printf ("\t5\t\t\tPolar\n");
    printf ("\t6\t\t\tHandkerchief\n");
    printf ("\t7\t\t\tHeart\n");
    printf ("\t8\t\t\tDisk\n");
    printf ("\t9\t\t\tSpiral\n");
    printf ("\t10\t\t\tHyperbolic\n");
    printf ("\t11\t\t\tDiamond\n");
    printf ("\t12\t\t\tEx\n");
    printf ("\t13\t\t\tJulia\n");
    printf ("\t14\t\t\tBent\n");
    printf ("\t15\t\t\tWaves\n");
    printf ("\t16\t\t\tFisheye\n");
    printf ("\t17\t\t\tPopcorn\n");
    printf ("\t18\t\t\tExponential\n");
    printf ("\t19\t\t\tPower\n");
    printf ("\t20\t\t\tCosine\n");
    printf ("\t21\t\t\tRings\n");
    printf ("\t22\t\t\tFan\n");
    printf ("\t23\t\t\tEyefish\n");
    printf ("\t24\t\t\tBubble\n");
    printf ("\t25\t\t\tCylinder\n");
    printf ("\t26\t\t\tTangent\n");
    printf ("\t27\t\t\tCross\n");
    printf ("\t28\t\t\tCollatz\n");
    fflush (stdout);
}

void
parse_args (int argc, char **argv, flame *fractal) {
    int i = 1;
    int override = 0;

    while (i < argc) {
        if (!strcmp (argv[i], "-h")) {
            print_usage ();
            exit (EXIT_SUCCESS);
        }
        else if (!strcmp (argv[i], "-I")) {
            fractal->invert = 1;
            i++;
        }
        else if (!strcmp (argv[i], "-R")) {
            fractal->seed = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-T")) {
            fractal->num_threads = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-f")) {
            fractal->file = argv[i + 1];
            i += 2;
        }
        else if (!strcmp (argv[i], "-x")) {
            fractal->xres = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-y")) {
            fractal->yres = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-m")) {
            fractal->xmin = (double) atof (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-M")) {
            fractal->xmax = (double) atof (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-l")) {
            fractal->ymin = (double) atof (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-L")) {
            fractal->ymax = (double) atof (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-S")) {
            fractal->symmetry = atoi (argv[i + 1]);
            i += 2;
            if (fractal->symmetry <= 0)
                fractal->symmetry = 1;
        }
        else if (!strcmp (argv[i], "-n")) {
            fractal->n = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-s")) {
            fractal->samples = atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-i")) {
            fractal->iterations =
                    atol (argv[i + 1]) < 20 ? 1000 : atol (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-p")) {
            if (((fractal->pallete = fopen (argv[i + 1], "r")) == NULL)) {
                // printf ("Error reading input file %s.\n", argv[i + 1]);
                LOGES("parse_args", "Error reading input file\n");
                exit (EXIT_FAILURE);
            }
            i += 2;
        }
        else if (!strcmp (argv[i], "-c")) {
            if (((fractal->cofile = fopen (argv[i + 1], "r")) == NULL)) {
                // printf ("Error reading input file %s.\n", argv[i + 1]);
                LOGES("parse_args", "Error reading input file\n");
                exit (EXIT_FAILURE);
            }
            i += 2;
        }
        else if (!strcmp (argv[i], "-r")) {
            fractal->R = atoi (argv[i + 1]) % 256;
            i += 2;
        }
        else if (!strcmp (argv[i], "-g")) {
            fractal->G = atoi (argv[i + 1]) % 256;
            i += 2;
        }
        else if (!strcmp (argv[i], "-b")) {
            fractal->B = atoi (argv[i + 1]) % 256;
            i += 2;
        }
        else if (!strcmp (argv[i], "-G")) {
            fractal->gamma =
                    (((double) atof (argv[i + 1])) ==
                     0.0) ? 2.2 : (double) atof (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-sup")) {
            fractal->sup = atoi (argv[i + 1]) == 0 ? 1 : atoi (argv[i + 1]);
            i += 2;
        }
        else if (!strcmp (argv[i], "-v")) {
            if (!override && fractal->count == 1) {            /* first user supplied param overrides default */
                fractal->choice[0] = atoi (argv[i + 1]);
                override = 1;
            }
            else {
                fractal->choice =
                        realloc (fractal->choice,
                                 ((fractal->count + 1) * sizeof (int)));
                if (fractal->choice == NULL) {
                    // printf
                    //        ("Error: malloc() failed in parse_args. Tried to allocate %d * %ld bytes.\n",
                    //         fractal->count, sizeof (int));
                    LOGES("parse_args", "Error: malloc() failed in parse_args\n");
                    exit (EXIT_FAILURE);
                }
                fractal->choice[fractal->count] = atoi (argv[i + 1]);
                fractal->count++;
            }
            // printf ("using trasformation number %d\n", atoi (argv[i + 1]));
            i += 2;
        }
        else {
            print_usage ();
            exit (EXIT_FAILURE);
        }
    }
}

double
modulus (double a, double b) {
    int cast;
    cast = (int) (a / b);
    return a - ((double) cast * b);
}

void write_to_raw (flame *fractal) {
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;

    /* this is a pointer to one row of image data */
    FILE *outfile = fopen( fractal->file, "wb" );

    cinfo.err = jpeg_std_error( &jerr );
    jpeg_create_compress(&cinfo);
    jpeg_stdio_dest(&cinfo, outfile);

    /* Setting the parameters of the output file here */
    cinfo.image_width = (JDIMENSION) fractal->xres;//width;
    cinfo.image_height = (JDIMENSION) fractal->yres;//height;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;

    jpeg_set_defaults( &cinfo );
    /* Now do the compression .. */
    jpeg_start_compress( &cinfo, TRUE );

    JSAMPROW buffer;
    int row, col;
    int invert = fractal->invert;

    char * colors = malloc (fractal->xres * 3 * sizeof (char));
    if (colors == NULL) {
        // printf ("malloc() failed in write_to_raw for color.\n");
        LOGES("write_to_raw", "malloc() failed in write_to_raw for color.\n");
        exit (EXIT_FAILURE);
    }

    for (row = 0; row < (*fractal).yres; row++) {
        for (col = 0; col < (*fractal).xres; col++) {
            int cr = invert == 1 ? ~((*fractal).pixels[row][col].r) : (*fractal).pixels[row][col].r;
            int cg = invert == 1 ? ~((*fractal).pixels[row][col].g) : (*fractal).pixels[row][col].g;
            int cb = invert == 1 ? ~((*fractal).pixels[row][col].b) : (*fractal).pixels[row][col].b;
            // color [col] = ((cr & 0xff) << 16) + ((cg & 0xff) << 8) + (cb & 0xff);
            colors [col * 3] = (char) cr;
            colors [col * 3 + 1] = (char) cg;
            colors [col * 3 + 2] = (char) cb;
        }

        jpeg_write_scanlines(&cinfo, (JSAMPARRAY) &colors, 1 );
        free (fractal->pixels[row]);
    }

    jpeg_finish_compress( &cinfo );
    jpeg_destroy_compress( &cinfo );

    fflush (outfile);
    fclose( outfile );

    free (colors);
}

void *
render (void *fract) {
    double r, theta, x, y, c, f, b, e;
    double newx, newy, pa1, pa2, pa3, pa4;
    double P0, P1, prefix, t;
    int i, k, num, s;
    long int step;
    unsigned int tran;
    flame *fractal = (flame *) fract;

    tran = 0;
    i = 0;

    for (num = 0; num < fractal->samples; num++) {
        newx = RANDR (fractal->xmin, fractal->xmax);
        newy = RANDR (fractal->ymin, fractal->ymax);

        for (step = -20; step < fractal->iterations; step++) {
            k = fractal->choice[tran % fractal->count];
            tran++;
            i = random () % fractal->n;    /*RANDR(0,fractal->n); */


            pa1 = fractal->coarray[i].pa1;
            pa2 = fractal->coarray[i].pa2;
            pa3 = fractal->coarray[i].pa3;
            pa4 = fractal->coarray[i].pa4;

            c = fractal->coarray[i].cc;
            f = fractal->coarray[i].fc;
            b = fractal->coarray[i].bc;
            e = fractal->coarray[i].ec;

            x =
                    fractal->coarray[i].ac * newx + fractal->coarray[i].bc * newy +
                    fractal->coarray[i].cc;
            y =
                    fractal->coarray[i].dc * newx + fractal->coarray[i].ec * newy +
                    fractal->coarray[i].fc;

            switch (k) {
                case 0:        /* Linear */
                    newx = x;
                    newy = y;
                    break;
                case 1:        /* Sinusoidal */
                    newx = sin (x);
                    newy = sin (y);
                    break;
                case 2:        /* Spherical */
                    r = 1.0 / (x * x + y * y);
                    newx = r * x;
                    newy = r * y;
                    break;
                case 3:        /* Swirl */
                    r = x * x + y * y;
                    newx = x * sin (r) - y * cos (r);
                    newy = x * cos (r) + y * sin (r);
                    break;
                case 4:        /* Horseshoe */
                    r = 1.0 / sqrt (x * x + y * y);
                    newx = r * (x - y) * (x + y);
                    newy = r * 2.0 * x * y;
                    break;
                case 5:        /* Polar */
                    newx = atan2 (y, x) / M_PI;
                    newy = sqrt (x * x + y * y) - 1.0;
                    break;
                case 6:        /* Handkerchief */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = r * sin (theta + r);
                    newy = r * cos (theta - r);
                    break;
                case 7:        /* Heart */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = r * sin (theta * r);
                    newy = -r * cos (theta * r);
                    break;
                case 8:        /* Disk */
                    r = sqrt (x * x + y * y) * M_PI;
                    theta = atan2 (y, x) / M_PI;
                    newx = theta * sin (r);
                    newy = theta * cos (r);
                    break;
                case 9:        /* Spiral */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = (1.0 / r) * (cos (theta) + sin (r));
                    newy = (1.0 / r) * (sin (theta) - cos (r));
                    break;
                case 10:        /* Hyperbolic */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = sin (theta) / r;
                    newy = r * cos (theta);
                    break;
                case 11:        /* Diamond */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = sin (theta) * cos (r);
                    newy = cos (theta) * sin (r);
                    break;
                case 12:        /* Ex */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    P0 = sin (theta + r);
                    P0 = P0 * P0 * P0;
                    P1 = cos (theta - r);
                    P1 = P1 * P1 * P1;
                    newx = r * (P0 + P1);
                    newy = r * (P0 - P1);
                    break;
                case 13:        /* Julia */
                    r = sqrt (sqrt (x * x + y * y));
                    theta = atan2 (y, x) * .5;
                    if (random_bit ())
                        theta += M_PI;
                    newx = r * cos (theta);
                    newy = r * sin (theta);
                    break;
                case 14:        /* Bent */
                    if (x >= 0.0 && y >= 0.0) {
                        newx = x;
                        newy = y;
                    }
                    else if (x < 0.0 && y >= 0.0) {
                        newx = 2.0 * x;
                        newy = y;
                    }
                    else if (x >= 0.0 && y < 0.0) {
                        newx = x;
                        newy = y * .5;
                    }
                    else if (x < 0.0 && y < 0.0) {
                        newx = 2.0 * x;
                        newy = y * .5;
                    }
                    break;
                case 15:        /* Waves */
                    newx = x + pa1 * sin (y / (pa2 * pa2));
                    newy = y + pa3 * sin (x / (pa4 * pa4));
                    break;
                case 16:        /* Fisheye */
                    r = 2.0 / (1. + sqrt (x * x + y * y));
                    newx = r * y;
                    newy = r * x;
                    break;
                case 17:        /* Popcorn */
                    newx = x + c * sin (tan (3.0 * y));
                    newy = y + f * sin (tan (3.0 * x));
                    break;
                case 18:        /* Exponential */
                    newx = exp (x - 1.0) * cos (M_PI * y);
                    newy = exp (x - 1.0) * sin (M_PI * y);
                    break;
                case 19:        /* Power */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx = pow (r, sin (theta)) * cos (theta);
                    newy = pow (r, sin (theta)) * sin (theta);
                    break;
                case 20:        /* Cosine */
                    newx = cos (M_PI * x) * cosh (y);
                    newy = -sin (M_PI * x) * sinh (y);
                    break;
                case 21:        /* Rings */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    prefix =
                            modulus ((r + pa2 * pa2),
                                     (2.0 * pa2 * pa2)) - (pa2 * pa2) + (r * (1.0 -
                                                                              pa2 * pa2));
                    newx = prefix * cos (theta);
                    newy = prefix * sin (theta);
                    break;
                case 22:        /* Fan */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    t = M_PI * c * c;
                    if (modulus (theta, t) > (t * .5)) {
                        newx = r * cos (theta - (t * .5));
                        newy = r * sin (theta - (t * .5));
                    }
                    else {
                        newx = r * cos (theta + (t * .5));
                        newy = r * sin (theta + (t * .5));
                    }
                    break;
                case 23:        /* Eyefish */
                    r = 2.0 / (1. + sqrt (x * x + y * y));
                    newx = r * x;
                    newy = r * y;
                    break;
                case 24:        /* Bubble */
                    r = 4 + x * x + y * y;
                    newx = (4.0 * x) / r;
                    newy = (4.0 * y) / r;
                    break;
                case 25:        /* Cylinder */
                    newx = sin (x);
                    newy = y;
                    break;
                case 26:        /* Tangent */
                    newx = sin (x) / cos (y);
                    newy = tan (y);
                    break;
                case 27:        /* Cross */
                    r = sqrt (1.0 / ((x * x - y * y) * (x * x - y * y)));
                    newx = x * r;
                    newy = y * r;
                    break;
                case 28:        /* Collatz */
                    newx = .25 * (1.0 + 4.0 * x - (1.0 + 2.0 * x) * cos (M_PI * x));
                    newy = .25 * (1.0 + 4.0 * y - (1.0 + 2.0 * y) * cos (M_PI * y));
                    break;
                case 29:        /* Mobius */
                    t = (pa3 * x + pa4) * (pa3 * x + pa4) + pa3 * y * pa3 * y;
                    newx =
                            ((pa1 * x + pa2) * (pa3 * x + pa4) + pa1 * pa3 * y * y) / t;
                    newy =
                            (pa1 * y * (pa3 * x + pa4) - pa3 * y * (pa1 * x + pa2)) / t;
                    break;
                case 30:        /* Blob */
                    r = sqrt (x * x + y * y);
                    theta = atan2 (y, x);
                    newx =
                            r * (pa2 +
                                 0.5 * (pa1 - pa2) * (sin (pa3 * theta) +
                                                      1)) * cos (theta);
                    newy =
                            r * (pa2 +
                                 0.5 * (pa1 - pa2) * (sin (pa3 * theta) +
                                                      1)) * sin (theta);
                    break;
                case 31:        /* Noise */
                    theta = RANDR (0, 1.);
                    r = RANDR (0, 1.);
                    newx = theta * x * cos (2 * M_PI * r);
                    newy = theta * y * sin (2 * M_PI * r);
                    break;
                case 32:        /* Blur */
                    theta = RANDR (0, 1.);
                    r = RANDR (0, 1.);
                    newx = theta * cos (2 * M_PI * r);
                    newy = theta * sin (2 * M_PI * r);
                    break;
                case 33:        /* Square */
                    newx = RANDR (0, 1.) - 0.5;
                    newy = RANDR (0, 1.) - 0.5;
                    break;
                case 34:        /* Not Broken Waves */
                    newx = x + b * sin (y / pow (c, 2.0));
                    newy = y + e * sin (x / pow (f, 2.0));
                    break;
                case 35:        /* something something */
                    newx = y;
                    newy = sin (x);
                    break;
                default:
                    break;
            }

            if (step > 0) {
                unsigned int x1, y1;
                unsigned char red, green, blue;
                pixel *point;
                double theta2, x_rot, y_rot;

                theta2 = 0.0;

                for (s = 0; s < fractal->symmetry; s++) {

                    theta2 += ((2 * M_PI) / (fractal->symmetry));
                    x_rot = newx * cos (theta2) - newy * sin (theta2);
                    y_rot = newx * sin (theta2) + newy * cos (theta2);

                    if (x_rot >= fractal->xmin && x_rot <= fractal->xmax
                        && y_rot >= fractal->ymin && y_rot <= fractal->ymax) {
                        x1 =
                                fractal->xres -
                                (unsigned
                                int) (((fractal->xmax -
                                        x_rot) / fractal->ranx) * fractal->xres);
                        y1 =
                                fractal->yres -
                                (unsigned
                                int) (((fractal->ymax -
                                        y_rot) / fractal->rany) * fractal->yres);

                        if ((int) x1 >= 0 && x1 < fractal->xres && (int) y1 >= 0
                            && y1 < fractal->yres) {
                            pthread_mutex_lock (&(fractal->lock[y1]));
                            point = &fractal->pixels[y1][x1];

                            if (!point->value.counter) {
                                point->r = fractal->coarray[i].r;
                                point->g = fractal->coarray[i].g;
                                point->b = fractal->coarray[i].b;
                            }
                            else {
                                red =
                                        (unsigned
                                        char) ((point->r +
                                                fractal->coarray[i].r) / 2);
                                point->r = red;
                                green =
                                        (unsigned
                                        char) ((point->g +
                                                fractal->coarray[i].g) / 2);
                                point->g = green;
                                blue =
                                        (unsigned
                                        char) ((point->b +
                                                fractal->coarray[i].b) / 2);
                                point->b = blue;
                            }
                            point->value.counter++;
                            pthread_mutex_unlock (&(fractal->lock[y1]));
                        }
                    }
                }
            }
        }
    }
    return NULL;
}

/* apply gamma color correction and log correction */
void
gamma_log (flame *fractal) {

    float max;
    int row, col;
    double gamma = fractal->gamma;

    max = 0.0;
    for (row = 0; row < fractal->yres; row++) {
        for (col = 0; col < fractal->xres; col++) {
            if (fractal->pixels[row][col].value.counter != 0) {
                fractal->pixels[row][col].value.normal =
                        (float) log10 ((double) fractal->pixels[row][col].
                                value.counter);
                if (fractal->pixels[row][col].value.normal > max)
                    max = fractal->pixels[row][col].value.normal;
            }
        }
    }

    for (row = 0; row < fractal->yres; row++) {
        for (col = 0; col < fractal->xres; col++) {
            fractal->pixels[row][col].value.normal /= max;
            fractal->pixels[row][col].r =
                    (unsigned char) ((float) (fractal->pixels[row][col].r)) *
                    pow (fractal->pixels[row][col].value.normal, (1.0 / gamma));
            fractal->pixels[row][col].g =
                    (unsigned char) ((float) (fractal->pixels[row][col].g)) *
                    pow (fractal->pixels[row][col].value.normal, (1.0 / gamma));
            fractal->pixels[row][col].b =
                    (unsigned char) ((float) (fractal->pixels[row][col].b)) *
                    pow (fractal->pixels[row][col].value.normal, (1.0 / gamma));
        }
    }

}


/* take a fractal rendered with larger bit bucket and shrink it */
void
reduce (flame *fractal) {
    unsigned int R, G, B, count, y, x;
    int sx, sy;
    int old_yres;
    int sample = fractal->sup;
    pixel **reduction;
    old_yres = (*fractal).yres;
    (*fractal).xres = (*fractal).xres / sample;
    (*fractal).yres = (*fractal).yres / sample;
    reduction = malloc ((*fractal).yres * sizeof (pixel *));
    if (reduction == NULL) {
        LOGES("reduce", "malloc() failed\n");
        exit (EXIT_FAILURE);
    }
    for (y = 0; y < (*fractal).yres; y++) {
        reduction[y] = malloc ((*fractal).xres * sizeof (pixel));
        if (reduction[y] == NULL) {
            // printf ("malloc() failed\n");
            LOGES("reduce", "malloc() failed\n");
            exit (EXIT_FAILURE);
        }
    }

    /* simple grid algorithm anti-aliasing */
    /* numerical average of colors in a square region */

    for (y = 0; y < (*fractal).yres; y++) {
        for (x = 0; x < (*fractal).xres; x++) {
            R = 0;
            G = 0;
            B = 0;
            count = 0;
            for (sy = 0; sy < sample; sy++) {
                for (sx = 0; sx < sample; sx++) {
                    R += (*fractal).pixels[y * sample + sy][x * sample + sx].r;
                    G += (*fractal).pixels[y * sample + sy][x * sample + sx].g;
                    B += (*fractal).pixels[y * sample + sy][x * sample + sx].b;
                    count +=
                            (*fractal).pixels[y * sample + sy][x * sample +
                                                               sx].value.counter;
                }
            }
            reduction[y][x].r = (unsigned char) (R / (sample * sample));
            reduction[y][x].g = (unsigned char) (G / (sample * sample));
            reduction[y][x].b = (unsigned char) (B / (sample * sample));
            reduction[y][x].value.counter = count;
        }
    }

    /* replace pixel array with new, smaller array */

    for (y = 0; y < old_yres; y++) {
        free ((*fractal).pixels[y]);
    }
    free ((*fractal).pixels);
    (*fractal).pixels = reduction;
}


JNIEXPORT jboolean JNICALL Java_org_brainail_EverboxingSplashFlame_utils_tool_ToolFractal_warmUp(
    JNIEnv * env,
    jobject obj,
    jstring filePath,
    jint xres,
    jint yres,
    jint vtype) {

    const char * strPath = NULL;
    strPath = (*env)->GetStringUTFChars(env, filePath, (jboolean *) 0);
    LOGIS("warmUp", strPath);

    flame fractal;
    // LOGIS("warmUp", "code line > flame fractal;");
    int i;
    // LOGIS("warmUp", "code line > int i;");
    pthread_t *threads;
    // LOGIS("warmUp", "code line > pthread_t *threads;");

    // >>> Init argv
    int argc;
    char ** argv = malloc(sizeof *argv * 10);

    char s_xres [64]; sprintf(s_xres, "%d", xres);
    char s_yres [64]; sprintf(s_yres, "%d", yres);
    char s_vtype [64]; sprintf(s_vtype, "%d", vtype);
    argv [1] = "-f";
    argv [2] = strPath;
    argv [3] = "-x";
    argv [4] = s_xres;
    argv [5] = "-y";
    argv [6] = s_yres;
    argv [7] = "-v";
    argv [8] = s_vtype;
    argc = 9;
    // <<< Init argv

    /* initialize our Flame Fractal */
    // printf ("Initializing...\n");
    LOGIS("warmUp", "Initializing...\n");
    fractal_init (&fractal);
    // printf ("Initialized!\n");
    LOGIS("warmUp", "Initialized!\n");

    /* parse arguments from the command line */
    // printf ("Parsing user arguments...\n");
    LOGIS("warmUp", "Parsing user arguments...\n");
    parse_args (argc, argv, &fractal);
    // printf ("Done!\n");
    LOGIS("warmUp", "Done!\n");

    /* seed the randomizer */
    srandom (fractal.seed);
    srand48 (random ());

    /* initalize the random coefficients */
    // printf ("Initialzing Coefficients and Colors...\n");
    LOGIS("warmUp", "Initialzing Coefficients and Colors...\n");
    coeff_init (&fractal);
    // printf ("Done!\n");
    LOGIS("warmUp", "Done!\n");

    /* allocate our memory buffer */
    // printf ("Allocating memory...\n");
    LOGIS("warmUp", "Allocating memory...\n");
    buffer_init (&fractal);
    LOGIS("warmUp", "Done!\n");
    // printf ("Done!\n");

    /* correct for threads */
    if (fractal.num_threads <= 0) {
        fractal.num_threads = 1;
    }
    threads = (pthread_t *) malloc (fractal.num_threads * sizeof (pthread_t));
    if (threads == NULL) {
        // printf ("Error: malloc() failed in main.\n");
        LOGES("warmUp", "Error: malloc() failed in main.\n");
        exit (EXIT_FAILURE);
    }
    fractal.samples /= fractal.num_threads;

    /* render the image */
    for (i = 0; i < fractal.num_threads; i++) {
        // printf ("Spawing thread %d\n", i);
        LOGIS("warmUp", "Spawing threads ...\n");
        if (0 != pthread_create (&threads[i], NULL, render, (void *) &fractal))
            exit (EXIT_FAILURE);
    }

    for (i = 0; i < fractal.num_threads; i++) {
        // printf ("Joining thread %d\n", i);
        LOGIS("warmUp", "Joining threads ...\n");
        if (0 != pthread_join (threads[i], NULL))
            exit (EXIT_FAILURE);
    }

    /* gamma and log correct */
    LOGIS("warmUp", "Finializing and writing out...\n");
    // printf ("Finializing and writing out...\n");
    gamma_log (&fractal);
    if (fractal.sup > 1) {
        reduce (&fractal);
    }

    /* write out the file */
    /*gamma_log (&fractal); */
    write_to_raw (&fractal);
    /* clean up */
    free (threads);
    free (fractal.lock);
    free (fractal.coarray);
    free (fractal.choice);
    free (fractal.pixels);
    // printf ("Done!\n");
    LOGIS("warmUp", "Done!\n");

    (*env)->ReleaseStringUTFChars(env, filePath, strPath);

    LOGIS("warmUp", "Bye-bye ...!\n");
    return JNI_TRUE;
}

